package com.itsoninc.das.common.sms.codec;

import static com.itsoninc.das.common.sms.codec.SMSCodecUtils.*;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.apache.commons.lang3.Validate;

import com.itsoninc.das.common.sms.model.InvalidUsageSummaryResponseRecord;
import com.itsoninc.das.common.sms.model.MessagingUsageSummaryResponseRecord;
import com.itsoninc.das.common.sms.model.UsageReportingSummaryResponseMessage;
import com.itsoninc.das.common.sms.model.VoiceUsageSummaryResponseRecord;

abstract class UsageReportingSummaryResponseMessageCodec {

	protected static final byte CMD_CODE = 0x02;

	private static final byte CODE_CORRELATION_ID = 0x01;
	private static final byte CODE_VOICE_USAGE_SUMMARY = 0x02;
	private static final byte CODE_MESSAGING_USAGE_SUMMARY = 0x03;
	private static final byte CODE_INVALID_USAGE = 0x04;

	protected static byte[] encode(UsageReportingSummaryResponseMessage message) {
		// Determine size of buffer to allocate
		int payloadLen = 18 // Correlation ID
				+ message.getVoiceUsageRecords().size() * 20 // Voice usage records
				+ message.getMessagingUsageRecords().size() * 20 // Messaging usage records
				+ message.getInvalidUsageRecords().size() * 12; // Invalid usage records

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
		for(VoiceUsageSummaryResponseRecord record : message.getVoiceUsageRecords()) {
			buffer.put(encodeVoiceUsageRecord(record));
		}

		// Write messaging summary records
		for(MessagingUsageSummaryResponseRecord record : message.getMessagingUsageRecords()) {
			buffer.put(encodeMessagingUsageRecord(record));
		}

		// Write invalid usage records
		for(InvalidUsageSummaryResponseRecord record : message.getInvalidUsageRecords()) {
			buffer.put(encodeInvalidUsageRecord(record));
		}

		// Make sure everything adds up
		Validate.isTrue(!buffer.hasRemaining());

		return result;
	}

	protected static UsageReportingSummaryResponseMessage decode(ByteBuffer buffer) {
		// Validate header
		Validate.isTrue(buffer.remaining() >= 3);
		Validate.isTrue(buffer.get() == CMD_CODE);
		Validate.isTrue(getUnsignedShort(buffer) == buffer.remaining());

		// Extract body
		UsageReportingSummaryResponseMessage result = new UsageReportingSummaryResponseMessage();
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
				VoiceUsageSummaryResponseRecord voiceRecord = decodeVoiceUsageRecord(subBuffer);
				result.getVoiceUsageRecords().add(voiceRecord);
				break;
			case CODE_MESSAGING_USAGE_SUMMARY:
				MessagingUsageSummaryResponseRecord messagingRecord = decodeMessagingUsageRecord(subBuffer);
				result.getMessagingUsageRecords().add(messagingRecord);
				break;
			case CODE_INVALID_USAGE:
				InvalidUsageSummaryResponseRecord invalidRecord = decodeInvalidUsageRecord(subBuffer);
				result.getInvalidUsageRecords().add(invalidRecord);
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

	protected static byte[] encodeVoiceUsageRecord(VoiceUsageSummaryResponseRecord record) {
		// Create the buffer
		byte[] result = new byte[20];
		ByteBuffer buffer = ByteBuffer.wrap(result);

		// Header
		buffer.put(CODE_VOICE_USAGE_SUMMARY);
		putUnsignedByte(buffer, result.length - 2);

		// Body
		buffer.putLong(record.getSubscriptionServicePolicyId());
		putUnsignedShort(buffer, record.getBillingPeriodId());
		putUnsignedInt(buffer, record.getSubscriberUsageInSeconds());
		putUnsignedInt(buffer, record.getTotalUsageInSeconds());

		// Make sure everything adds up
		Validate.isTrue(!buffer.hasRemaining());

		// Return
		return result;
	}

	protected static VoiceUsageSummaryResponseRecord decodeVoiceUsageRecord(ByteBuffer buffer) {
		// Validate header
		Validate.isTrue(buffer.remaining() >= 2);
		Validate.isTrue(buffer.get() == CODE_VOICE_USAGE_SUMMARY);
		Validate.isTrue(getUnsignedByte(buffer) == buffer.remaining());

		// Extract body
		VoiceUsageSummaryResponseRecord record = new VoiceUsageSummaryResponseRecord();
		record.setSubscriptionServicePolicyId(buffer.getLong());
		record.setBillingPeriodId(getUnsignedShort(buffer));
		record.setSubscriberUsageInSeconds(getUnsignedInt(buffer));
		record.setTotalUsageInSeconds(getUnsignedInt(buffer));

		// Return
		return record;
	}

	protected static byte[] encodeMessagingUsageRecord(MessagingUsageSummaryResponseRecord record) {
		// Create the buffer
		byte[] result = new byte[20];
		ByteBuffer buffer = ByteBuffer.wrap(result);

		// Header
		buffer.put(CODE_MESSAGING_USAGE_SUMMARY);
		putUnsignedByte(buffer, result.length - 2);

		// Body
		buffer.putLong(record.getSubscriptionServicePolicyId());
		putUnsignedShort(buffer, record.getBillingPeriodId());
		putUnsignedInt(buffer, record.getSubscriberUsageInMessages());
		putUnsignedInt(buffer, record.getTotalUsageInMessages());

		// Make sure everything adds up
		Validate.isTrue(!buffer.hasRemaining());

		// Return
		return result;
	}

	protected static MessagingUsageSummaryResponseRecord decodeMessagingUsageRecord(ByteBuffer buffer) {
		// Validate header
		Validate.isTrue(buffer.remaining() >= 2);
		Validate.isTrue(buffer.get() == CODE_MESSAGING_USAGE_SUMMARY);
		Validate.isTrue(getUnsignedByte(buffer) == buffer.remaining());

		// Extract body
		MessagingUsageSummaryResponseRecord record = new MessagingUsageSummaryResponseRecord();
		record.setSubscriptionServicePolicyId(buffer.getLong());
		record.setBillingPeriodId(getUnsignedShort(buffer));
		record.setSubscriberUsageInMessages(getUnsignedInt(buffer));
		record.setTotalUsageInMessages(getUnsignedInt(buffer));

		// Return
		return record;
	}

	protected static byte[] encodeInvalidUsageRecord(InvalidUsageSummaryResponseRecord record) {
		// Create the buffer
		byte[] result = new byte[12];
		ByteBuffer buffer = ByteBuffer.wrap(result);

		// Header
		buffer.put(CODE_INVALID_USAGE);
		putUnsignedByte(buffer, result.length - 2);

		// Body
		buffer.putLong(record.getSubscriptionServicePolicyId());
		putUnsignedShort(buffer, record.getBillingPeriodId());

		// Make sure everything adds up
		Validate.isTrue(!buffer.hasRemaining());

		// Return
		return result;
	}

	protected static InvalidUsageSummaryResponseRecord decodeInvalidUsageRecord(ByteBuffer buffer) {
		// Validate header
		Validate.isTrue(buffer.remaining() >= 2);
		Validate.isTrue(buffer.get() == CODE_INVALID_USAGE);
		Validate.isTrue(getUnsignedByte(buffer) == buffer.remaining());

		// Extract body
		InvalidUsageSummaryResponseRecord record = new InvalidUsageSummaryResponseRecord();
		record.setSubscriptionServicePolicyId(buffer.getLong());
		record.setBillingPeriodId(getUnsignedShort(buffer));

		// Return
		return record;
	}

}
