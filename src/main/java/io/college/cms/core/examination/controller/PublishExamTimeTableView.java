package io.college.cms.core.examination.controller;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.navigator.View;
import com.vaadin.ui.VerticalLayout;

import io.college.cms.core.examination.service.ExamTimeTableViewService;
import io.college.cms.core.ui.services.CoreUiService;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class PublishExamTimeTableView extends VerticalLayout implements View {
	private static final long serialVersionUID = 1L;
	private CoreUiService coreUi;
	private ExamTimeTableViewService examViewService;

	/**
	 * @param coreUi
	 * @param examViewService
	 */
	public PublishExamTimeTableView(CoreUiService coreUi, ExamTimeTableViewService examViewService) {
		super();
		this.coreUi = coreUi;
		this.examViewService = examViewService;
	}

	@PostConstruct
	protected void paint() {
	}

}
