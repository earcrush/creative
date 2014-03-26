package com.itsoninc.das.common.sms.codec;

import static com.itsoninc.das.common.sms.codec.SMSCodecUtils.*;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Date;

import org.apache.commons.lang3.Validate;

import com.itsoninc.das.common.sms.model.ScepEnrollmentCodeResponseMessage;

abstract class ScepEnrollmentCodeResponseMessageCodec {

	protected static final byte CMD_CODE = 0x03;

	private static final byte CODE_CODE = 0x01;
	private static final byte CODE_EXPIRATION = 0x02;

	protected static byte[] encode(ScepEnrollmentCodeResponseMessage message) throws UnsupportedEncodingException {
		// Convert enrollment code
		byte[] encodedCode = encodeEnrollmentCode(message.getCode());

		// Convert expiration date
		byte[] encodedExpiration = encodeExpirationDate(message.getExpiration());

		// Determine size of buffer to allocate
		int payloadLen = encodedCode.length + encodedExpiration.length;

		// Allocate the buffers
		byte[] result = new byte[3 + payloadLen];
		ByteBuffer buffer = ByteBuffer.wrap(result);

		// Write header
		buffer.put(CMD_CODE);
		putUnsignedShort(buffer, payloadLen);

		// Write enrollment code
		buffer.put(encodedCode);

		// Write expiration date
		buffer.put(encodedExpiration);

		// Make sure everything adds up
		Validate.isTrue(!buffer.hasRemaining());

		return result;
	}

	protected static ScepEnrollmentCodeResponseMessage decode(ByteBuffer buffer) throws UnsupportedEncodingException {
		// Validate header
		Validate.isTrue(buffer.remaining() >= 3);
		Validate.isTrue(buffer.get() == CMD_CODE);
		Validate.isTrue(getUnsignedShort(buffer) == buffer.remaining());

		// Extract body
		ScepEnrollmentCodeResponseMessage result = new ScepEnrollmentCodeResponseMessage();
		while(buffer.hasRemaining()) {
			ByteBuffer subBuffer = buffer.slice();

			// Validate and extract header info
			Validate.isTrue(buffer.remaining() >= 2);
			byte code = buffer.get();
			int length = getUnsignedByte(buffer);

			// Set sub buffer length
			subBuffer.limit(2 + length);

			switch(code) {
			case CODE_CODE:
				result.setCode(decodeEnrollmentCode(subBuffer));
				break;
			case CODE_EXPIRATION:
				result.setExpiration(decodeExpirationDate(subBuffer));
				break;
			}

			// Advance buffer to next token
			buffer.position(buffer.position() + length);
		}

		return result;
	}

	protected static byte[] encodeEnrollmentCode(String code) throws UnsupportedEncodingException {
		// Convert to UTF8
		byte[] codeUtf8 = code.getBytes("UTF-8");

		// Validate max length
		Validate.isTrue(codeUtf8.length <= 255);

		// Create the buffer
		byte[] result = new byte[2 + codeUtf8.length];
		ByteBuffer buffer = ByteBuffer.wrap(result);

		// Header
		buffer.put(CODE_CODE);
		putUnsignedByte(buffer, result.length - 2);

		// Body
		buffer.put(codeUtf8);

		// Make sure everything adds up
		Validate.isTrue(!buffer.hasRemaining());

		// Return
		return result;
	}

	protected static String decodeEnrollmentCode(ByteBuffer buffer) throws UnsupportedEncodingException {
		// Validate header
		Validate.isTrue(buffer.remaining() >= 2);
		Validate.isTrue(buffer.get() == CODE_CODE);
		int length = getUnsignedByte(buffer);
		Validate.isTrue(length == buffer.remaining());

		// Extract body
		byte[] codeUtf8 = new byte[length];
		buffer.get(codeUtf8);

		// Return
		return new String(codeUtf8, "UTF-8");
	}

	protected static byte[] encodeExpirationDate(Date expiration) {
		// Expiration is optional
		if(expiration == null) {
			return new byte[0];
		}

		// Create the buffer
		byte[] result = new byte[2 + 8];
		ByteBuffer buffer = ByteBuffer.wrap(result);

		// Header
		buffer.put(CODE_EXPIRATION);
		putUnsignedByte(buffer, result.length - 2);

		// Body
		buffer.putLong(expiration.getTime());

		// Make sure everything adds up
		Validate.isTrue(!buffer.hasRemaining());

		// Return
		return result;
	}

	protected static Date decodeExpirationDate(ByteBuffer buffer) {
		// Validate header
		Validate.isTrue(buffer.remaining() >= 2);
		Validate.isTrue(buffer.get() == CODE_EXPIRATION);
		Validate.isTrue(getUnsignedByte(buffer) == buffer.remaining());

		// Extract body
		Date expiration = new Date(buffer.getLong());

		// Return
		return expiration;
	}

}
