package io.college.cms.core.application;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Service
@Builder()
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FactoryResponse {
	private SummaryMessageEnum summaryMessage;
	private Object response;
}
