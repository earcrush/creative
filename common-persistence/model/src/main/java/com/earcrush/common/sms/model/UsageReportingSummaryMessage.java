package com.itsoninc.das.common.sms.model;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class UsageReportingSummaryMessage extends SMSMessage {

	private String correlationId;

	private Collection<VoiceUsageSummaryRecord> voiceUsageSummaryRecords = new ArrayList<VoiceUsageSummaryRecord>();
	private Collection<MessagingUsageSummaryRecord> messagingUsageSummaryRecords = new ArrayList<MessagingUsageSummaryRecord>();

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public Collection<VoiceUsageSummaryRecord> getVoiceUsageSummaryRecords() {
		return voiceUsageSummaryRecords;
	}

	public void setVoiceUsageSummaryRecords(Collection<VoiceUsageSummaryRecord> voiceUsageSummaryRecords) {
		this.voiceUsageSummaryRecords = voiceUsageSummaryRecords;
	}

	public Collection<MessagingUsageSummaryRecord> getMessagingUsageSummaryRecords() {
		return messagingUsageSummaryRecords;
	}

	public void setMessagingUsageSummaryRecords(Collection<MessagingUsageSummaryRecord> messagingUsageSummaryRecords) {
		this.messagingUsageSummaryRecords = messagingUsageSummaryRecords;
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
