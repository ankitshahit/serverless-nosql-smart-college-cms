package io.college.cms.core.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.amazonaws.services.rekognition.model.IndexFacesRequest;
import com.amazonaws.services.rekognition.model.SearchFacesByImageRequest;

@Configuration
public class RecognitionConfiguration {
	private AppParams params;

	@Autowired
	public RecognitionConfiguration(AppParams params) {
		this.params = params;
	}

	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public SearchFacesByImageRequest searchFaces() {
		SearchFacesByImageRequest imageRequest = new SearchFacesByImageRequest();
		imageRequest.setCollectionId(params.getRecognitionCollectionId());
		return imageRequest;
	}

	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public IndexFacesRequest indexFaces() {
		return new IndexFacesRequest().withCollectionId(params.getRecognitionCollectionId())
				.withDetectionAttributes("DEFAULT");
	}
}
