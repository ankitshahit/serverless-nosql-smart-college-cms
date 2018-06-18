package io.college.cms.core.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@ComponentScan(basePackages = { "io.college.cms.core.*", "io.college.cms.core.configuration.*",
		"io.college.cms.core.examination.*", "io.college.cms.core.courses.*", "io.college.cms.core.application.*",
		"io.college.cms.core.application.automation.*", "io.college.cms.core.ui.*",
		"io.college.cms.core.dynamodbloader.*", "io.college.cms.core.dynamodbloader.service.*",
		"io.college.cms.core.faq.*" })
@Slf4j
public class Application {
	public static void main(String[] args) {
		LOGGER.info("isDebugEnabled = {}", LOGGER.isDebugEnabled());
		System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");

		SpringApplication.run(Application.class, args);
	}
}
