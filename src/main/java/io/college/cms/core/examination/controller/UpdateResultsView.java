package io.college.cms.core.examination.controller;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSingleSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.Utils;
import io.college.cms.core.examination.model.ResultModel;
import io.college.cms.core.examination.service.ExamResponseService;
import io.college.cms.core.ui.builder.VaadinWrapper;
import io.college.cms.core.ui.listener.EmptyFieldListener;
import io.college.cms.core.ui.services.CoreUiService;
import io.college.cms.core.user.service.SecurityService;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class UpdateResultsView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;
	private Label totalMarks;
	private TextField marksFld;
	private ComboBox<String> usernamesCb;
	private ComboBox<String> examsCb;
	private ComboBox<String> subjectsCb;
	private Button updateBtn;
	private Label resultLbl;
	private ComboBox<String> subjectTypeCb;
	private SecurityService securityService;
	private CoreUiService uiService;
	private ApplicationContext app;
	private Binder<ResultModel> binder;

	@Autowired
	public UpdateResultsView(SecurityService securityService, CoreUiService uiService, ApplicationContext app) {
		super();
		this.securityService = securityService;
		this.uiService = uiService;
		this.app = app;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		String params = event.getParameters();

		if (StringUtils.isNotEmpty(params)) {
			String[] splitParams = params.split("/");
			ResultModel model = ResultModel.builder().actionBy(securityService.getPrincipal()).examName(splitParams[0])
					.subjectName(splitParams[1]).subjectType(splitParams[2]).username(splitParams[3]).build();
			binder.readBean(model);
		} else {
			uiService.setExamsName(examsCb);
		}
	}

	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		View.super.beforeLeave(event);
	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	protected void paint() {

		totalMarks = VaadinWrapper.builder().caption("Total marks").build().label();
		marksFld = VaadinWrapper.builder().caption("Marks acquired").build().textField();
		usernamesCb = (ComboBox<String>) VaadinWrapper.builder().caption("usernames").placeholder("type username")
				.build().comboBox();
		examsCb = (ComboBox<String>) VaadinWrapper.builder().caption("exams").placeholder("type exam name").build()
				.comboBox();
		subjectsCb = (ComboBox<String>) VaadinWrapper.builder().caption("subjects").placeholder("type subject name")
				.build().comboBox();
		subjectTypeCb = (ComboBox<String>) VaadinWrapper.builder().caption("Subject Type").placeholder("type ").build()
				.comboBox();
		updateBtn = VaadinWrapper.builder().caption("Update").enabled(false).build().button();
		resultLbl = VaadinWrapper.builder().caption("Status").build().label();

		binder = new Binder<>();
		binder.bind(marksFld, ResultModel::getMarks, ResultModel::setMarks);
		binder.bind(usernamesCb, ResultModel::getUsername, ResultModel::setUsername);
		binder.bind(examsCb, ResultModel::getExamName, ResultModel::setExamName);
		binder.bind(subjectsCb, ResultModel::getSubjectName, ResultModel::setSubjectName);
		binder.bind(subjectTypeCb, ResultModel::getSubjectType, ResultModel::setSubjectType);

		VerticalLayout rootLayout = new VerticalLayout(this.examsCb, this.subjectsCb, this.subjectTypeCb,
				this.usernamesCb, this.totalMarks, this.marksFld, this.resultLbl, this.updateBtn);
		Panel rootPanel = new Panel();
		rootPanel.setContent(rootLayout);
		addComponent(rootPanel);
		this.marksFld.addValueChangeListener(value -> {
			String marks = value.getValue();
			String maxMarks = totalMarks.getValue();
			if (StringUtils.isNotEmpty(marks) && StringUtils.isNotEmpty(maxMarks)) {
				// TODO: IMPLEMENT A PASS MARK SYSTEM BASED UPON VALUES ENTERED?
			}
		});
		updateBtn.addClickListener(click -> {
			ExamResponseService examResponseService = app.getBean(ExamResponseService.class);
			ResultModel model = ResultModel.builder().build();
			try {
				binder.writeBean(model);
				FactoryResponse fr = examResponseService.updateMarks(model);
				Utils.showFactoryResponseMsg(fr);
			} catch (ValidationException e) {
				Utils.showErrorNotification("Error unable to update marks");
				return;
			}
		});

		attachEmptyFieldListener(this.marksFld);
		attachEmptyFieldListener(this.subjectsCb);
		attachEmptyFieldListener(this.examsCb);
		attachEmptyFieldListener(this.subjectTypeCb);
		attachEmptyFieldListener(this.usernamesCb);
	}

	private void attachEmptyFieldListener(AbstractField<String> field) {
		EmptyFieldListener<String> listener = new EmptyFieldListener<>();
		listener.setSourceField(field);
		listener.setMandatoryFields(this.marksFld);
		listener.setMandatoryListFields(this.subjectsCb, this.examsCb, this.subjectTypeCb, this.usernamesCb);
		listener.setTargetBtn(updateBtn);
		field.addValueChangeListener(listener);
	}

	private void attachEmptyFieldListener(AbstractSingleSelect<String> field) {
		EmptyFieldListener<String> listener = new EmptyFieldListener<>();
		listener.setSourceListField(field);
		listener.setMandatoryFields(this.marksFld);
		listener.setMandatoryListFields(this.subjectsCb, this.examsCb, this.subjectTypeCb, this.usernamesCb);
		listener.setTargetBtn(updateBtn);
		field.addValueChangeListener(listener);
	}
}
