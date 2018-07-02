package io.college.cms.core.attendance.services;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.vaadin.server.FileResource;

import io.college.cms.core.attendance.model.AttendanceModel;
import io.college.cms.core.configuration.AppParams;
import io.college.cms.core.dynamodb.service.DynamoGenericService;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class AttendanceResponseService {
	private AppParams params;
	private ApplicationContext app;
	private DynamoGenericService<AttendanceModel, String> attendanceDbService;

	/**
	 * @param params
	 */
	public AttendanceResponseService(ApplicationContext app, AppParams params) {
		super();
		this.app = app;
		this.params = params;
	}

	@Async
	public void tag(FileResource resource, Consumer<List<String>> taggedStudents, Runnable progressListener,
			Runnable successListener) {

	}
}
