package com.itsoninc.das.common.sms.model;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class UsageReportingSummaryResponseMessage extends SMSMessage {

	private String correlationId;

	private Collection<VoiceUsageSummaryResponseRecord> voiceUsageRecords = new ArrayList<VoiceUsageSummaryResponseRecord>();
	private Collection<MessagingUsageSummaryResponseRecord> messagingUsageRecords = new ArrayList<MessagingUsageSummaryResponseRecord>();
	private Collection<InvalidUsageSummaryResponseRecord> invalidUsageRecords = new ArrayList<InvalidUsageSummaryResponseRecord>();

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public Collection<VoiceUsageSummaryResponseRecord> getVoiceUsageRecords() {
		return voiceUsageRecords;
	}

	public void setVoiceUsageRecords(Collection<VoiceUsageSummaryResponseRecord> voiceUsageRecords) {
		this.voiceUsageRecords = voiceUsageRecords;
	}

	public Collection<MessagingUsageSummaryResponseRecord> getMessagingUsageRecords() {
		return messagingUsageRecords;
	}

	public void setMessagingUsageRecords(
			Collection<MessagingUsageSummaryResponseRecord> messagingUsageRecords) {
		this.messagingUsageRecords = messagingUsageRecords;
	}

	public Collection<InvalidUsageSummaryResponseRecord> getInvalidUsageRecords() {
		return invalidUsageRecords;
	}

	public void setInvalidUsageRecords(
			Collection<InvalidUsageSummaryResponseRecord> invalidUsageRecords) {
		this.invalidUsageRecords = invalidUsageRecords;
	}

	@Override
	public boolean equals(Object rhs) {
		return EqualsBuilder.reflectionEquals(this, rhs);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
