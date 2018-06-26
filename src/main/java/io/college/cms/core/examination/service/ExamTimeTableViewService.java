package io.college.cms.core.examination.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import io.college.cms.core.examination.model.PublishExamViewDTO;
import io.college.cms.core.ui.services.CoreUiService;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class ExamTimeTableViewService {
	private CoreUiService coreUi;
	private PublishExamViewDTO examViewDTO;
	/**
	 * @param coreUi
	 */
	@Autowired
	public ExamTimeTableViewService(CoreUiService coreUi) {
		super();
		this.coreUi = coreUi;
	}
}
