package io.college.cms.core.application.automation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.college.cms.core.examination.db.ExaminationModel;
import io.college.cms.core.examination.db.ExaminationModel.ExamSubject;
import io.college.cms.core.exception.ApplicationException;

@Service
public class GenerateJson {
	private ObjectMapper objectMapper;

	@Autowired
	public GenerateJson(ObjectMapper mapper) {
		this.objectMapper = mapper;
	}

	public String withModel(Object data) throws ApplicationException {

		String json = "";
		try {
			json = objectMapper.writeValueAsString(data);
		} catch (JsonProcessingException e) {
			throw new ApplicationException(e);
		}
		return json;
	}

	public static void main(String[] args) throws ApplicationException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		System.out.println(new GenerateJson(mapper)
				.withModel(ExaminationModel.builder().withSubject(ExamSubject.builder().build())));
	}
}
