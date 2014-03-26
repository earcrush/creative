package com.itsoninc.das.common.sms.codec;

import static com.itsoninc.das.common.sms.codec.SMSCodecUtils.*;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.apache.commons.lang3.Validate;

import com.itsoninc.das.common.sms.model.MessagingUsageSummaryRecord;
import com.itsoninc.das.common.sms.model.UsageReportingSummaryMessage;
import com.itsoninc.das.common.sms.model.VoiceUsageSummaryRecord;

abstract class UsageReportingSummaryMessageCodec {

	protected static final byte CMD_CODE = 0x01;

	private static final byte CODE_CORRELATION_ID = 0x01;
	private static final byte CODE_VOICE_USAGE_SUMMARY = 0x02;
	private static final byte CODE_MESSAGING_USAGE_SUMMARY = 0x03;

	protected static byte[] encode(UsageReportingSummaryMessage message) {
		// Determine size of buffer to allocate
		int payloadLen = 18 // Correlation ID
				+ message.getVoiceUsageSummaryRecords().size() * 16 // Voice summary records
				+ message.getMessagingUsageSummaryRecords().size() * 16; // Messaging summary records

		// Allocate the buffers
		byte[] result = new byte[3 + payloadLen];
		ByteBuffer buffer = ByteBuffer.wrap(result);

		// Write header
		buffer.put(CMD_CODE);
		putUnsignedShort(buffer, payloadLen);

		// Write Correlation ID
		UUID correlationId = UUID.fromString(message.getCorrelationId());
		buffer.put(encodeCorrelationId(correlationId));

		// Write voice summary records
		for(VoiceUsageSummaryRecord record : message.getVoiceUsageSummaryRecords()) {
			buffer.put(encodeVoiceUsageSummaryRecord(record));
		}

		// Write messaging summary records
		for(MessagingUsageSummaryRecord record : message.getMessagingUsageSummaryRecords()) {
			buffer.put(encodeMessagingUsageSummaryRecord(record));
		}

		// Make sure everything adds up
		Validate.isTrue(!buffer.hasRemaining());

		return result;
	}

	protected static UsageReportingSummaryMessage decode(ByteBuffer buffer) {
		// Validate header
		Validate.isTrue(buffer.remaining() >= 3);
		Validate.isTrue(buffer.get() == CMD_CODE);
		Validate.isTrue(getUnsignedShort(buffer) == buffer.remaining());

		// Extract body
		UsageReportingSummaryMessage result = new UsageReportingSummaryMessage();
		while(buffer.hasRemaining()) {
			ByteBuffer subBuffer = buffer.slice();

			// Validate and extract header info
			Validate.isTrue(buffer.remaining() >= 2);
			byte code = buffer.get();
			int length = getUnsignedByte(buffer);

			// Set sub buffer length
			subBuffer.limit(2 + length);

			switch(code) {
			case CODE_CORRELATION_ID:
				UUID correlationId = decodeCorrelationId(subBuffer);
				result.setCorrelationId(correlationId.toString());
				break;
			case CODE_VOICE_USAGE_SUMMARY:
				VoiceUsageSummaryRecord voiceRecord = decodeVoiceUsageSummaryRecord(subBuffer);
				result.getVoiceUsageSummaryRecords().add(voiceRecord);
				break;
			case CODE_MESSAGING_USAGE_SUMMARY:
				MessagingUsageSummaryRecord messagingRecord = decodeMessagingUsageSummaryRecord(subBuffer);
				result.getMessagingUsageSummaryRecords().add(messagingRecord);
				break;
			}

			// Advance buffer to next token
			buffer.position(buffer.position() + length);
		}

		return result;
	}

	protected static byte[] encodeCorrelationId(UUID correlationId) {
		// Create the buffer
		byte[] result = new byte[18];
		ByteBuffer buffer = ByteBuffer.wrap(result);

		// Header
		buffer.put(CODE_CORRELATION_ID);
		putUnsignedByte(buffer, result.length - 2);

		// Body
		buffer.putLong(correlationId.getMostSignificantBits());
		buffer.putLong(correlationId.getLeastSignificantBits());

		// Make sure everything adds up
		Validate.isTrue(!buffer.hasRemaining());

		// Return
		return result;
	}

	protected static UUID decodeCorrelationId(ByteBuffer buffer) {
		// Validate header
		Validate.isTrue(buffer.remaining() >= 2);
		Validate.isTrue(buffer.get() == CODE_CORRELATION_ID);
		Validate.isTrue(getUnsignedByte(buffer) == buffer.remaining());

		// Extract body
		long mostSigBits = buffer.getLong();
		long leastSigBits = buffer.getLong();
		UUID uuid = new UUID(mostSigBits, leastSigBits);

		// Return
		return uuid;
	}

	protected static byte[] encodeVoiceUsageSummaryRecord(VoiceUsageSummaryRecord record) {
		// Create the buffer
		byte[] result = new byte[16];
		ByteBuffer buffer = ByteBuffer.wrap(result);

		// Header
		buffer.put(CODE_VOICE_USAGE_SUMMARY);
		putUnsignedByte(buffer, result.length - 2);

		// Body
		buffer.putLong(record.getSubscriptionServicePolicyId());
		putUnsignedShort(buffer, record.getBillingPeriodId());
		putUnsignedInt(buffer, record.getUsageInSeconds());

		// Make sure everything adds up
		Validate.isTrue(!buffer.hasRemaining());

		// Return
		return result;
	}

	protected static VoiceUsageSummaryRecord decodeVoiceUsageSummaryRecord(ByteBuffer buffer) {
		// Validate header
		Validate.isTrue(buffer.remaining() >= 2);
		Validate.isTrue(buffer.get() == CODE_VOICE_USAGE_SUMMARY);
		Validate.isTrue(getUnsignedByte(buffer) == buffer.remaining());

		// Extract body
		VoiceUsageSummaryRecord record = new VoiceUsageSummaryRecord();
		record.setSubscriptionServicePolicyId(buffer.getLong());
		record.setBillingPeriodId(getUnsignedShort(buffer));
		record.setUsageInSeconds(getUnsignedInt(buffer));

		// Return
		return record;
	}

	protected static byte[] encodeMessagingUsageSummaryRecord(MessagingUsageSummaryRecord record) {
		// Create the buffer
		byte[] result = new byte[16];
		ByteBuffer buffer = ByteBuffer.wrap(result);

		// Header
		buffer.put(CODE_MESSAGING_USAGE_SUMMARY);
		putUnsignedByte(buffer, result.length - 2);

		// Body
		buffer.putLong(record.getSubscriptionServicePolicyId());
		putUnsignedShort(buffer, record.getBillingPeriodId());
		putUnsignedInt(buffer, record.getUsageInMessages());

		// Make sure everything adds up
		Validate.isTrue(!buffer.hasRemaining());

		// Return
		return result;
	}

	protected static MessagingUsageSummaryRecord decodeMessagingUsageSummaryRecord(ByteBuffer buffer) {
		// Validate header
		Validate.isTrue(buffer.remaining() >= 2);
		Validate.isTrue(buffer.get() == CODE_MESSAGING_USAGE_SUMMARY);
		Validate.isTrue(getUnsignedByte(buffer) == buffer.remaining());

		// Extract body
		MessagingUsageSummaryRecord record = new MessagingUsageSummaryRecord();
		record.setSubscriptionServicePolicyId(buffer.getLong());
		record.setBillingPeriodId(getUnsignedShort(buffer));
		record.setUsageInMessages(getUnsignedInt(buffer));

		// Return
		return record;
	}

}
