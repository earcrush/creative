package com.itsoninc.das.common.sms.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class MessagingUsageSummaryResponseRecord {
	private Long subscriptionServicePolicyId;
	private Integer billingPeriodId;

	private long subscriberUsageInMessages;
	private long totalUsageInMessages;

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

	public long getSubscriberUsageInMessages() {
		return subscriberUsageInMessages;
	}

	public void setSubscriberUsageInMessages(long subscriberUsageInMessages) {
		this.subscriberUsageInMessages = subscriberUsageInMessages;
	}

	public long getTotalUsageInMessages() {
		return totalUsageInMessages;
	}

	public void setTotalUsageInMessages(long totalUsageInMessages) {
		this.totalUsageInMessages = totalUsageInMessages;
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
