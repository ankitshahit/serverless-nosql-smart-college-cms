package io.college.cms.core.configuration;

import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Deprecated
@Configuration
public class AppConfiguration {
	@Setter
	@Getter
	private AppParams params;

	public AppConfiguration(@NonNull AppParams app) {
		this.params = app;
	}

	// @Bean()
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
		return mapper;
	}

}
