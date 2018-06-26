package io.college.cms.core.attendance.services;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import io.college.cms.core.configuration.AppParams;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class ImageRecongitionService {
	private AppParams params;

	/**
	 * @param params
	 */
	public ImageRecongitionService(AppParams params) {
		super();
		this.params = params;
	}

	public void addToCollection() {

	}
}
