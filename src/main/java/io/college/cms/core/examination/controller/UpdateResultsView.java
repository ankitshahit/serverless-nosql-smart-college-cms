package io.college.cms.core.examination.controller;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.Utils;
import io.college.cms.core.courses.controller.constants.SubjectType;
import io.college.cms.core.courses.db.CourseModel;
import io.college.cms.core.courses.db.CourseModel.SubjectModel;
import io.college.cms.core.courses.service.CourseResponseService;
import io.college.cms.core.examination.model.ExaminationModel;
import io.college.cms.core.examination.model.ResultModel;
import io.college.cms.core.examination.service.ExamResponseService;
import io.college.cms.core.ui.builder.VaadinWrapper;
import io.college.cms.core.ui.listener.EmptyFieldListener;
import io.college.cms.core.ui.model.ViewConstants;
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
	private ExamResponseService examResponseService;
	private CourseResponseService courseResponseService;
	private Label requiredLbl;
	private boolean result;
	@Autowired
	public UpdateResultsView(SecurityService securityService, CoreUiService uiService, ApplicationContext app) {
		super();
		this.securityService = securityService;
		this.uiService = uiService;
		this.app = app;
		this.examResponseService = app.getBean(ExamResponseService.class);
		this.courseResponseService = app.getBean(CourseResponseService.class);
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
			binder.setReadOnly(false);
			
			marksFld.setEnabled(true);
			//uiService.setItemsUser(usernamesCb);
			usernamesCb.setEnabled(false);
			examsCb.setEnabled(false);
			subjectsCb.setEnabled(false);
			subjectTypeCb.setEnabled(false);
			
		} else {
			uiService.setExamsName(examsCb);
			uiService.setItemsUser(usernamesCb);
		}
	}

	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		View.super.beforeLeave(event);
		binder.readBean(ResultModel.builder().build());
	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	protected void paint() {

		totalMarks = VaadinWrapper.builder().caption("Total marks").build().label();
		requiredLbl = VaadinWrapper.builder().caption("Required marks").build().label();
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
		subjectTypeCb.setItems(SubjectType.THEORY.toString(), SubjectType.PRACTICAL.toString(),
				SubjectType.INTERNAL.toString(), SubjectType.OTHER.toString());
		binder = new Binder<>();
		binder.bind(marksFld, ResultModel::getMarks, ResultModel::setMarks);
		binder.bind(usernamesCb, ResultModel::getUsername, ResultModel::setUsername);
		binder.bind(examsCb, ResultModel::getExamName, ResultModel::setExamName);
		binder.bind(subjectsCb, ResultModel::getSubjectName, ResultModel::setSubjectName);
		binder.bind(subjectTypeCb, ResultModel::getSubjectType, ResultModel::setSubjectType);

		this.updateBtn.setStyleName(ValoTheme.BUTTON_PRIMARY);
		VerticalLayout rootLayout = new VerticalLayout(this.examsCb, this.subjectsCb, this.subjectTypeCb,
				this.usernamesCb, new HorizontalLayout(this.totalMarks, requiredLbl), this.marksFld, this.resultLbl,
				this.updateBtn);

		rootLayout.setComponentAlignment(this.updateBtn, Alignment.BOTTOM_RIGHT);
		Panel rootPanel = new Panel();
		rootPanel.setContent(rootLayout);
		addComponent(rootPanel);

		this.examsCb.addSelectionListener(select -> {
			if (!examsCb.getOptionalValue().isPresent()) {
				return;
			}
			FactoryResponse fr = examResponseService.getExamByExamId(null, examsCb.getOptionalValue().get());
			if (Utils.isError(fr)) {
				Utils.showFactoryResponseOnlyError(fr);
				return;
			}
			ExaminationModel model = (ExaminationModel) fr.getResponse();
			uiService.setItemsSubject(subjectsCb, model.getCourseName(), model.getSemester());
		});

		this.subjectTypeCb.addSelectionListener(select -> {
			if (subjectTypeCb.getOptionalValue().isPresent() == false) {
				return;
			}
			FactoryResponse fr = examResponseService.getExamByExamId(null, examsCb.getOptionalValue().get());
			if (Utils.isError(fr)) {
				Utils.showFactoryResponseOnlyError(fr);
				return;
			}
			ExaminationModel model = (ExaminationModel) fr.getResponse();
			fr = courseResponseService.findByCourseName(null, model.getCourseName());
			if (Utils.isError(fr)) {
				Utils.showFactoryResponseOnlyError(fr);
				return;
			}
			CourseModel courseModel = (CourseModel) fr.getResponse();
			if (CollectionUtils.isEmpty(courseModel.getSubjects())) {
				return;
			}
			for (SubjectModel subjectModel : courseModel.getSubjects()) {
				if (!subjectModel.getSubjectName().equalsIgnoreCase(subjectsCb.getOptionalValue().get())) {
					continue;
				}
				if (SubjectType.THEORY.toString().equalsIgnoreCase(subjectTypeCb.getOptionalValue().get())) {
					totalMarks.setValue(String.valueOf(subjectModel.getTheory()));
					requiredLbl.setValue(String.valueOf(subjectModel.getTheoryMarksRequired()));
				} else if (SubjectType.PRACTICAL.toString().equalsIgnoreCase(subjectTypeCb.getOptionalValue().get())) {
					totalMarks.setValue(String.valueOf(subjectModel.getPractical()));
					requiredLbl.setValue(String.valueOf(subjectModel.getPracticalMarksRequired()));
				} else if (SubjectType.INTERNAL.toString().equalsIgnoreCase(subjectTypeCb.getOptionalValue().get())) {
					totalMarks.setValue(String.valueOf(subjectModel.getInternal()));
					requiredLbl.setValue(String.valueOf(subjectModel.getInternalMarksRequired()));
				} else if (SubjectType.OTHER.toString().equalsIgnoreCase(subjectTypeCb.getOptionalValue().get())) {
					totalMarks.setValue(String.valueOf(subjectModel.getOthers()));
					requiredLbl.setValue(String.valueOf(subjectModel.getOthersMarksRequired()));
				}
			}
		});

		updateBtn.addClickListener(click -> {
			ExamResponseService examResponseService = app.getBean(ExamResponseService.class);
			ResultModel model = ResultModel.builder().build();
			try {
				binder.writeBean(model);
				FactoryResponse fr = examResponseService.getExamByExamId(null, examsCb.getOptionalValue().get());
				if (Utils.isError(fr)) {
					Utils.showFactoryResponseOnlyError(fr);
					return;
				}
				ExaminationModel examDataModel = (ExaminationModel) fr.getResponse();
				model.setCourseName(examDataModel.getCourseName());
				model.setSemester(examDataModel.getSemester());
				model.setTotalMarks(totalMarks.getValue());
				model.setResult(result);
				fr = examResponseService.updateMarks(model);
				Notification notifi = Utils.showFactoryResponseMsg(fr);
				notifi.setCaption("Message");
				notifi.addCloseListener(close -> getUI().getNavigator().navigateTo(ViewConstants.SEE_RESULTS));

			} catch (ValidationException e) {
				Utils.showErrorNotification("Error unable to update marks");
				return;
			}
		});

		marksFld.addValueChangeListener(value -> {
			if (StringUtils.isEmpty(value.getValue())) {
				resultLbl.setValue("<b><p style=background-color:red;color:white>Fail</p></b>");
				return;
			}
			Double marks = Double.parseDouble(marksFld.getValue());
			if (Double.parseDouble(requiredLbl.getValue()) <= marks
					&& (Double.parseDouble(totalMarks.getValue()) >= marks)) {
				result = true;
				resultLbl.setValue("<b><p style=background-color:green;color:white>Pass</p></b>");
			} else if (Double.parseDouble(totalMarks.getValue()) >= marks && marks >= 0) {
				resultLbl.setValue("<b><p style=background-color:red;color:white>Fail</p></b>");
			} else {
				resultLbl.setValue("<b><p style=background-color:red;color:white>Not acceptable input.</p></b>");
			}
		});

		attachEmptyFieldListener(this.marksFld);
		attachEmptyFieldListener(this.subjectsCb);
		attachEmptyFieldListener(this.examsCb);
		attachEmptyFieldListener(this.subjectTypeCb);
		attachEmptyFieldListener(this.usernamesCb);
		EmptyFieldListener<String> usernameCbL = attachEmptyFieldListener(this.usernamesCb);
		this.usernamesCb.addValueChangeListener(usernameCbL);
		EmptyFieldListener<String> subjectsCbL = attachEmptyFieldListener(this.subjectsCb);
		this.subjectsCb.addValueChangeListener(subjectsCbL);
		EmptyFieldListener<String> examsCbL = attachEmptyFieldListener(this.examsCb);
		this.examsCb.addValueChangeListener(examsCbL);
		EmptyFieldListener<String> subjectTypeCbL = attachEmptyFieldListener(this.subjectTypeCb);
		this.subjectTypeCb.addValueChangeListener(subjectTypeCbL);
		EmptyFieldListener<String> marksFldL = attachEmptyFieldListener(this.marksFld);
		this.marksFld.addValueChangeListener(marksFldL);

	}

	private EmptyFieldListener<String> attachEmptyFieldListener(AbstractField<String> field) {
		EmptyFieldListener<String> listener = new EmptyFieldListener<>();
		listener.setSourceField(field);
		listener.setMandatoryFields(this.marksFld);
		listener.setMandatoryListFields(this.subjectsCb, this.examsCb, this.subjectTypeCb, this.usernamesCb);
		listener.setTargetBtn(updateBtn);
		return listener;
	}

	private EmptyFieldListener<String> attachEmptyFieldListener(AbstractSingleSelect<String> field) {
		EmptyFieldListener<String> listener = new EmptyFieldListener<>();
		listener.setSourceListField(field);
		listener.setMandatoryFields(this.marksFld);
		listener.setMandatoryListFields(this.subjectsCb, this.examsCb, this.subjectTypeCb, this.usernamesCb);
		listener.setTargetBtn(updateBtn);
		return listener;
	}
}
