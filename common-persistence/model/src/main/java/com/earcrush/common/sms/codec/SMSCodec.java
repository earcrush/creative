package com.itsoninc.das.common.sms.codec;

import static com.itsoninc.das.common.sms.codec.SMSCodecUtils.*;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.Validate;

import com.itsoninc.das.common.sms.model.PushNotificationMessage;
import com.itsoninc.das.common.sms.model.SMSMessage;
import com.itsoninc.das.common.sms.model.ScepEnrollmentCodeResponseMessage;
import com.itsoninc.das.common.sms.model.UsageReportingSummaryMessage;
import com.itsoninc.das.common.sms.model.UsageReportingSummaryResponseMessage;

public abstract class SMSCodec {

	// THESE CONSTANTS MUST NEVER CHANGE
	// "a" is for ascii, but is unused
	// "b" is for binary - encrypted with AES and mbase64 encoded
	// "c" is for binary - mbase64 encoded
	// "z" is reserved as an escape sequence
	private static final String HEADER_ASCII = "itson:a";
	private static final String HEADER_ENCRYPTED_BASE64 = "itson:b";
	private static final String HEADER_BASE64 = "itson:c";

	public static boolean isEncrypted(String message) {
		return message.startsWith(HEADER_ENCRYPTED_BASE64);
	}

	public static String encode(SMSMessage message, byte[] key) throws SMSCodecException {
		// Convert message to binary
		byte[] binaryMessage;
		try {
			if(message instanceof UsageReportingSummaryMessage) {
				binaryMessage = UsageReportingSummaryMessageCodec.encode((UsageReportingSummaryMessage)message);
			} else if(message instanceof UsageReportingSummaryResponseMessage) {
				binaryMessage = UsageReportingSummaryResponseMessageCodec.encode((UsageReportingSummaryResponseMessage)message);
			} else if(message instanceof PushNotificationMessage) {
				binaryMessage = PushNotificationMessageCodec.encode((PushNotificationMessage)message);
			} else {
				throw new IllegalArgumentException("Unsupported type: " + message.getClass().getName());
			}
		} catch(Exception e) {
			throw new SMSCodecException("Could not encode message", e);
		}

		return encodeInternalEncryptedBase64(binaryMessage, key);
	}

	public static String encode(SMSMessage message) throws SMSCodecException {
		// Convert message to binary
		byte[] binaryMessage;
		try {
			if(message instanceof ScepEnrollmentCodeResponseMessage) {
				binaryMessage = ScepEnrollmentCodeResponseMessageCodec.encode((ScepEnrollmentCodeResponseMessage)message);
			} else {
				throw new IllegalArgumentException("Unsupported type: " + message.getClass().getName());
			}
		} catch(Exception e) {
			throw new SMSCodecException("Could not encode message", e);
		}

		return encodeInternalBase64(binaryMessage);
	}

	protected static String encodeInternalEncryptedBase64(byte[] data, byte[] key) throws SMSCodecException {
		// Encrypt
		byte[] encrypted = encrypt(data, key);

		// Base64 encode
		String encoded = new String(Base64.encodeBase64(encrypted));

		// Prepend header;
		return HEADER_ENCRYPTED_BASE64 + encoded;
	}

	protected static String encodeInternalBase64(byte[] data) throws SMSCodecException {
		// Base64 encode
		String encoded = new String(Base64.encodeBase64(data));

		// Prepend header;
		return HEADER_BASE64 + encoded;
	}

	public static SMSMessage decode(String str, byte[] key) throws SMSCodecException {
		byte[] decoded = null;
		if(str.startsWith(HEADER_ENCRYPTED_BASE64)) {
			decoded = decodeInternalEncryptedBase64(str, key);
		} else if(str.startsWith(HEADER_BASE64)) {
			decoded = decodeInternalBase64(str);
		} else {
			throw new SMSCodecException("Unsupported encoding");
		}

		// Extract body
		// Note: Only decode the first command TLV.  Ignore (but allow) others.
		try {
			ByteBuffer buffer = ByteBuffer.wrap(decoded);

			// Validate and extract TL header
			Validate.isTrue(buffer.remaining() >= 3);
			byte code = buffer.get();
			int length = getUnsignedShort(buffer);
			Validate.isTrue(buffer.remaining() >= length);

			// Unread TL header
			buffer.rewind();

			// Set buffer length for decode
			buffer.limit(3 + length);

			// Decode TLV
			SMSMessage message = null;
			switch(code) {
			case UsageReportingSummaryMessageCodec.CMD_CODE:
				message = UsageReportingSummaryMessageCodec.decode(buffer);
				break;
			case UsageReportingSummaryResponseMessageCodec.CMD_CODE:
				message = UsageReportingSummaryResponseMessageCodec.decode(buffer);
				break;
			case ScepEnrollmentCodeResponseMessageCodec.CMD_CODE:
				message = ScepEnrollmentCodeResponseMessageCodec.decode(buffer);
				break;
			case PushNotificationMessageCodec.CMD_CODE:
				message = PushNotificationMessageCodec.decode(buffer);
				break;
			default:
				throw new IllegalArgumentException("Unsupported code: " + code);
			}

			return message;
		} catch(Exception e) {
			throw new SMSCodecException("Could not decode message", e);
		}
	}

	protected static byte[] decodeInternalEncryptedBase64(String str, byte[] key) throws SMSCodecException {
		// Must start with correct header
		Validate.isTrue(str.startsWith(HEADER_ENCRYPTED_BASE64));

		// Strip header and base64 decode
		String payload = str.substring(HEADER_ENCRYPTED_BASE64.length());
		byte[] decoded = Base64.decodeBase64(payload.getBytes());

		// Decrypt
		return decrypt(decoded, key);
	}

	protected static byte[] decodeInternalBase64(String str) throws SMSCodecException {
		// Must start with correct header
		Validate.isTrue(str.startsWith(HEADER_BASE64));

		// Strip header and base64 decode
		String payload = str.substring(HEADER_BASE64.length());
		byte[] decoded = Base64.decodeBase64(payload.getBytes());

		return decoded;
	}

	protected static byte[] encrypt(byte[] data, byte[] key) throws SMSCodecException {
		try {
			Cipher c = getCipher(Cipher.ENCRYPT_MODE, key);

			// Allocate buffers for CRC + data
			byte[] toEncrypt = new byte[4 + data.length];

			// Copy data
			System.arraycopy(data, 0, toEncrypt, 4, data.length);

			// Calculate CRC
			CRC32 crc32 = new CRC32();
			crc32.update(data);
			long crcValue = crc32.getValue();

			// Write CRC
			ByteBuffer buffer = ByteBuffer.wrap(toEncrypt);
			putUnsignedInt(buffer, crcValue);

			// Encrypt
			byte[] encrypted = c.doFinal(toEncrypt);

			// Done
			return encrypted;
		} catch(Exception e) {
			throw new SMSCodecException("Failure encrypting data", e);
		}
	}

	protected static byte[] decrypt(byte[] data, byte[] key) throws SMSCodecException {
		try {
			Cipher c = getCipher(Cipher.DECRYPT_MODE, key);

			// Decrypt
			byte[] decrypted = c.doFinal(data);

			// Extract payload
			byte[] result = new byte[decrypted.length - 4];
			System.arraycopy(decrypted, 4, result, 0, result.length);

			// Read CRC
			ByteBuffer buffer = ByteBuffer.wrap(decrypted);
			Validate.isTrue(buffer.remaining() >= 4);
			long crcValue = getUnsignedInt(buffer);

			// Validate CRC
			CRC32 crc32 = new CRC32();
			crc32.update(result);
			long crcExpected = crc32.getValue();
			Validate.isTrue(crcExpected == crcValue, "Invalid CRC");

			// Done
			return result;
		} catch(Exception e) {
			throw new SMSCodecException("Failure decrypting data", e);
		}
	}

	protected static Cipher getCipher(int mode, byte[] rawKey) throws Exception {
		Validate.isTrue(rawKey.length == 16, "Key must be 16 bytes");

		// Convert key
		SecretKey key = new SecretKeySpec(rawKey, "AES");

		// AES in CBC (chaining) mode with PKCS5 padding
		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");

		// Note: Using an IV of all 0s, equivalent to ECB mode.  An IV serves to hide the fact that
		//       the same data is sent twice.  This is not in our threat model, so we save bytes by
		//       not transmitting an IV along with the encrypted data.
		c.init(mode, key, new IvParameterSpec(new byte[16]));

		// Return cipher
		return c;
	}

}
