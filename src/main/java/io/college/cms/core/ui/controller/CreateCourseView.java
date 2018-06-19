package io.college.cms.core.ui.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Composite;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.courses.db.CourseModel;
import io.college.cms.core.courses.db.CourseModel.SubjectModel;
import io.college.cms.core.courses.service.CourseResponseService;
import io.college.cms.core.courses.service.CourseVaadinService;
import io.college.cms.core.ui.builder.DeletePopupView;
import io.college.cms.core.ui.model.CourseDTO;
import io.college.cms.core.ui.services.ICoursesService;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class CreateCourseView extends Composite implements View, ICoursesService {

	private static final long serialVersionUID = 1L;

	private CourseResponseService courseResp;

	private CourseVaadinService courseUIService;
	private CourseModel courseModel;
	private CourseDTO courseStepOne;
	private CourseDTO courseStepTwo;
	private VerticalLayout verticalLayout;

	@Autowired
	public CreateCourseView(CourseResponseService resp, CourseVaadinService uiService) {
		this.courseResp = resp;
		this.courseUIService = uiService;
	}

	public void setCourseModel(CourseModel courseModel) {
		this.courseModel = courseModel;
	}

	@Autowired
	public void setCourseUIService(CourseVaadinService courseUIService) {
		this.courseUIService = courseUIService;
	}

	@Autowired
	public void setCourseResp(CourseResponseService courseResp) {
		this.courseResp = courseResp;
	}

	@PostConstruct()
	public void paint() {
		verticalLayout = new VerticalLayout();
		courseStepOne = courseUIService.courseMetaDataStep1();
		courseStepTwo = courseUIService.courseMetadataStep2();
		Accordion accordin = new Accordion();
		accordin.setTabsVisible(true);
		VerticalLayout step2 = courseUIService.buildCoursePageTwo(courseUIService.courseMetadataStep2());

		// we are tab 1 that is coursecreatestep1 in a different method to
		// increase readbility
		courseStepOne(accordin, step2);
		courseStepTwo(accordin, null);

		accordin.setResponsive(true);
		verticalLayout.addComponent(accordin);
		verticalLayout.setComponentAlignment(accordin, Alignment.TOP_LEFT);

		setCompositionRoot(verticalLayout);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		try {

			LOGGER.debug("CreateCourseView is triggered");
			if (courseModel != null) {
				courseStepOne.getCourseName().setValue(courseModel.getCourseName());
				courseStepOne.getCourseDescription().setValue(courseModel.getDescription());
				courseStepOne.getIsArchive().setValue(courseModel.isArchive());
				courseStepOne.getMaxStudents().setValue(String.valueOf(courseModel.getMaxStudentsAllowed()));
				if (CollectionUtils.isNotEmpty(courseModel.getSubjects())) {
					List<String> collection = new ArrayList<String>();
					for (SubjectModel subject : courseModel.getSubjects()) {
						collection.add(subject.getSubjectName());

					}
					courseStepOne.getAddedSubjects().setItems(collection);
				}

			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			Notification notifi = Notification.show("", Type.ERROR_MESSAGE);
			notifi.setCaption("Application error");
			notifi.setIcon(VaadinIcons.STOP_COG);
			notifi.setDescription(
					"We were unable to process request for some reason! Please try again later or contact admin");
			notifi.setDelayMsec(Notification.DELAY_FOREVER);
		}
	}

	private void courseStepOne(Accordion accordin, Component step2) {

		accordin.addTab(courseUIService.buildCoursePageOne(courseStepOne), "Create course			(1/3)");
		courseStepOne.getSaveCourse().setEnabled(false);
		courseUIService.attachEmptyValueListenerCourseStep1(courseStepOne);
		courseStepOne.getReset().addClickListener(click -> {
			DeletePopupView deleteView = new DeletePopupView();
			deleteView.show(getUI(), clickData -> {
				deleteView.setVisible(false);
				FactoryResponse fr = courseResp.deleteCourse(courseStepOne.getCourseName().getValue());
				if (fr == null || SummaryMessageEnum.SUCCESS != fr.getSummaryMessage()) {
					deleteView.getUnsuccessfullNotification(String.valueOf(fr.getResponse()));
				} else {
					deleteView.getDeleteNotification();
				}
				clickData.getComponent().setVisible(false);
				clickData.getComponent().getParent().setVisible(false);
			});
		});
		
		courseStepOne.getSaveCourse().addClickListener(click -> {
			if (click.getSource() != courseStepOne.getSaveCourse()) {
				return;
			}
			if (!courseStepOne.getMaxStudents().getOptionalValue().isPresent()
					|| !StringUtils.isNumber(courseStepOne.getMaxStudents().getOptionalValue().get())) {
				Notification.show("Max seats available is not provided or is not a number", Type.ERROR_MESSAGE);
			}
			FactoryResponse data = courseResp.saveCourseMetadata(CourseModel.builder()
					.courseName(courseStepOne.getCourseName().getOptionalValue().get())
					.description(courseStepOne.getCourseDescription().getOptionalValue().get())
					.isArchive(courseStepOne.getIsArchive().getValue())
					.maxStudentsAllowed(Long.valueOf(courseStepOne.getMaxStudents().getOptionalValue().get())).build());

			boolean result = io.college.cms.core.application.SummaryMessageEnum.SUCCESS != data.getSummaryMessage()
					&& data.getResponse() instanceof String;

			if (result) {
				Notification.show("Validation error: " + data.getResponse(), Type.ERROR_MESSAGE);
			} else {

				Notification notifi = Notification.show("Course created", Type.HUMANIZED_MESSAGE);
				notifi.setIcon(VaadinIcons.CHECK);
				notifi.setDelayMsec(Notification.DELAY_FOREVER);
				notifi.setHtmlContentAllowed(true);
				notifi.setDescription(
						"A course has been created with metadata of coursename, description, max seats available and archive?\n Click here to dismiss and move to <b>step2</b>");
				notifi.addCloseListener(close -> {
					accordin.setSelectedTab(step2);
				});
			}

		});
	}

	private void courseStepTwo(Accordion accord, Component step3) {

		courseStepTwo.getSubjectAttributes().addValueChangeListener(change -> {
			boolean internal = false;
			boolean theory = false;
			boolean practical = false;
			boolean others = false;
			if (CollectionUtils.isNotEmpty(change.getValue())) {
				internal = change.getValue().contains("Internal");
				theory = change.getValue().contains("Theory");
				practical = change.getValue().contains("Practical");
				others = change.getValue().contains("Others");
			}

			courseStepTwo.getTheoryMarks().setVisible(theory);
			courseStepTwo.getTheoryPassMarks().setVisible(theory);
			courseStepTwo.getPracticalMarks().setVisible(practical);
			courseStepTwo.getPracticalPassMarks().setVisible(practical);
			courseStepTwo.getInternalMarks().setVisible(internal);
			courseStepTwo.getInternalPassMarks().setVisible(internal);
			courseStepTwo.getOtherMarks().setVisible(others);
			courseStepTwo.getOtherPassMarks().setVisible(others);
		});
		VerticalLayout layout = courseUIService.buildCoursePageTwo(courseStepTwo);
		accord.addTab(layout, "Create course			(2/3)");
	}

	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		View.super.beforeLeave(event);
	}
}
