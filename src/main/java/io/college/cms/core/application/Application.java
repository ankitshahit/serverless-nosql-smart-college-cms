package io.college.cms.core.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "io.college.cms.core.*", "io.college.cms.core.configuration.*",
		"io.college.cms.core.examination.*", "io.college.cms.core.courses.*", "io.college.cms.core.application.*",
		"io.college.cms.core.application.automation.*" })
public class Application {
	public static void main(String[] args) {
		System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
		SpringApplication.run(Application.class, args);
	}
}
