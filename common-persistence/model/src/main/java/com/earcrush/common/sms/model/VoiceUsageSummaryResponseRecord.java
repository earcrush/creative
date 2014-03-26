package com.itsoninc.das.common.sms.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class VoiceUsageSummaryResponseRecord {
	private Long subscriptionServicePolicyId;
	private Integer billingPeriodId;

	private long subscriberUsageInSeconds;
	private long totalUsageInSeconds;

	public Long getSubscriptionServicePolicyId() {
		return subscriptionServicePolicyId;
	}

	public void setSubscriptionServicePolicyId(Long subscriptionServicePolicyId) {
		this.subscriptionServicePolicyId = subscriptionServicePolicyId;
	}

	public Integer getBillingPeriodId() {
		return billingPeriodId;
	}

	public void setBillingPeriodId(Integer billingPeriodId) {
		this.billingPeriodId = billingPeriodId;
	}

	public long getSubscriberUsageInSeconds() {
		return subscriberUsageInSeconds;
	}

	public void setSubscriberUsageInSeconds(long subscriberUsageInSeconds) {
		this.subscriberUsageInSeconds = subscriberUsageInSeconds;
	}

	public long getTotalUsageInSeconds() {
		return totalUsageInSeconds;
	}

	public void setTotalUsageInSeconds(long totalUsageInSeconds) {
		this.totalUsageInSeconds = totalUsageInSeconds;
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
