package io.college.cms.core;

import org.springframework.stereotype.Service;

import lombok.Builder;

@Service
@Builder
public class FactoryResponse {
	private SummaryMessageEnum summaryMessage;
	private Object response;

	public SummaryMessageEnum getSummaryMessage() {
		return summaryMessage;
	}

	public void setSummaryMessage(SummaryMessageEnum summaryMessage) {
		this.summaryMessage = summaryMessage;
	}

	public Object getResponse() {
		return response;
	}

	public void setResponse(Object response) {
		this.response = response;
	}

}
