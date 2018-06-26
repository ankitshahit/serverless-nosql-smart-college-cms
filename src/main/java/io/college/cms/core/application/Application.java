package io.college.cms.core.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@ComponentScan(basePackages = { "io.college.cms.core.*", "io.college.cms.core.configuration.*",
		"io.college.cms.core.examination.*", "io.college.cms.core.courses.*", "io.college.cms.core.application.*",
		"io.college.cms.core.application.automation.*", "io.college.cms.core.ui.*",
		"io.college.cms.core.dynamodbloader.*", "io.college.cms.core.dynamodbloader.service.*",
		"io.college.cms.core.faq.*", "io.college.cms.core.admission.*", "io.college.cms.core.ui.services.*",
		"io.college.cms.core.upload.*", "io.college.cms.core.job.*", "io.college.cms.core.attendance.*" })
@Slf4j
@EnableCaching()
public class Application {
	public static void main(String[] args) {
		LOGGER.debug("isDebugEnabled = {}", LOGGER.isDebugEnabled());
		System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
		SpringApplication.run(Application.class, args);
	}

	@Configuration
	public class SpringConfiguration {
		@Bean
		public CacheManager cacheManager() {
			return new ConcurrentMapCacheManager();
		}
	}
}
