package com.itsoninc.das.common.sms.codec;

import static com.itsoninc.das.common.sms.codec.SMSCodecUtils.*;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Date;

import org.apache.commons.lang3.Validate;

import com.itsoninc.das.common.sms.model.PushNotificationMessage;

abstract class PushNotificationMessageCodec {

	protected static final byte CMD_CODE = 0x04;

	private static final byte CODE_TIMESTAMP = 0x01;

	protected static byte[] encode(PushNotificationMessage message) throws UnsupportedEncodingException {
		// Convert timestamp
		byte[] encodedTimestamp = encodeTimestamp(message.getTimestamp());

		// Determine size of buffer to allocate
		int payloadLen = encodedTimestamp.length;

		// Allocate the buffers
		byte[] result = new byte[3 + payloadLen];
		ByteBuffer buffer = ByteBuffer.wrap(result);

		// Write header
		buffer.put(CMD_CODE);
		putUnsignedShort(buffer, payloadLen);

		// Write timestamp
		buffer.put(encodedTimestamp);

		// Make sure everything adds up
		Validate.isTrue(!buffer.hasRemaining());

		return result;
	}

	protected static PushNotificationMessage decode(ByteBuffer buffer) throws UnsupportedEncodingException {
		// Validate header
		Validate.isTrue(buffer.remaining() >= 3);
		Validate.isTrue(buffer.get() == CMD_CODE);
		Validate.isTrue(getUnsignedShort(buffer) == buffer.remaining());

		// Extract body
		PushNotificationMessage result = new PushNotificationMessage();
		while(buffer.hasRemaining()) {
			ByteBuffer subBuffer = buffer.slice();

			// Validate and extract header info
			Validate.isTrue(buffer.remaining() >= 2);
			byte code = buffer.get();
			int length = getUnsignedByte(buffer);

			// Set sub buffer length
			subBuffer.limit(2 + length);

			switch(code) {
			case CODE_TIMESTAMP:
				result.setTimestamp(decodeTimestamp(subBuffer));
				break;
			}

			// Advance buffer to next token
			buffer.position(buffer.position() + length);
		}

		return result;
	}

	protected static byte[] encodeTimestamp(Date timestamp) {
		// Timestamp is optional
		if(timestamp == null) {
			return new byte[0];
		}

		// Create the buffer
		byte[] result = new byte[2 + 8];
		ByteBuffer buffer = ByteBuffer.wrap(result);

		// Header
		buffer.put(CODE_TIMESTAMP);
		putUnsignedByte(buffer, result.length - 2);

		// Body
		buffer.putLong(timestamp.getTime());

		// Make sure everything adds up
		Validate.isTrue(!buffer.hasRemaining());

		// Return
		return result;
	}

	protected static Date decodeTimestamp(ByteBuffer buffer) {
		// Validate header
		Validate.isTrue(buffer.remaining() >= 2);
		Validate.isTrue(buffer.get() == CODE_TIMESTAMP);
		Validate.isTrue(getUnsignedByte(buffer) == buffer.remaining());

		// Extract body
		Date timestamp = new Date(buffer.getLong());

		// Return
		return timestamp;
	}

}
