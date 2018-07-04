package io.college.cms.core.ui.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.application.Utils;
import io.college.cms.core.courses.controller.constants.SubjectType;
import io.college.cms.core.courses.db.CourseModel;
import io.college.cms.core.courses.db.CourseModel.SubjectModel;
import io.college.cms.core.courses.service.CourseResponseService;
import io.college.cms.core.courses.service.CourseVaadinService;
import io.college.cms.core.ui.builder.DeletePopupView;
import io.college.cms.core.ui.model.CourseDTO;
import io.college.cms.core.ui.services.ICoursesService;
import io.college.cms.core.ui.util.ListenerUtility;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class PublishCourseView extends VerticalLayout implements View, ICoursesService {

	private static final long serialVersionUID = 1L;

	private CourseResponseService courseResponseService;
	private CourseVaadinService courseUIService;
	private CourseModel courseModel;
	private CourseDTO courseStepOne;
	private CourseDTO courseStepTwo;
	private VerticalLayout verticalLayout;
	private List<String> subjects;
	private List<SubjectModel> subjectModels;

	@Autowired
	public PublishCourseView(CourseResponseService resp, CourseVaadinService uiService) {
		this.courseResponseService = resp;
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
		this.courseResponseService = courseResp;
	}

	@PostConstruct()
	public void paint() {

		subjects = new ArrayList<>();
		subjectModels = new ArrayList<>();
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

		accordin.setResponsive(false);
		verticalLayout.addComponent(accordin);
		verticalLayout.setComponentAlignment(accordin, Alignment.MIDDLE_CENTER);

		addComponent(verticalLayout);

		courseStepTwo.getAddedSubjects().addValueChangeListener(singleSelect -> {
			if (!ListenerUtility.isValidSourceEvent(singleSelect.getComponent(), courseStepTwo.getAddedSubjects())) {
				return;
			}

			courseStepTwo.getRemoveSubject()
					.setVisible(CollectionUtils.isNotEmpty(courseStepTwo.getAddedSubjects().getSelectedItems()));

		});
		courseStepTwo.getRemoveSubject().addClickListener(click -> {
			if (!ListenerUtility.isValidSourceEvent(click.getComponent(), courseStepTwo.getRemoveSubject())) {
				return;
			}
			if (CollectionUtils.isEmpty(courseStepTwo.getAddedSubjects().getSelectedItems())) {
				return;
			}
			for (String item : courseStepTwo.getAddedSubjects().getSelectedItems()) {
				subjects.remove(item);
				Iterator<SubjectModel> iterator = subjectModels.iterator();
				while (iterator.hasNext()) {
					SubjectModel subjectModel = iterator.next();
					if (subjectModel != null && (item != null && !item.isEmpty())
							&& item.equalsIgnoreCase(subjectModel.getSubjectName())) {
						iterator.remove();
						break;
					}
				}
			}
			courseStepTwo.getAddedSubjects().setItems(subjects);
		});
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		try {
			LOGGER.debug("CreateCourseView is triggered");
			courseStepTwo.getAddedSubjects().setSizeFull();
			courseStepOne.getReset().setVisible(false);
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
					courseStepTwo.getAddedSubjects().setItems(collection);
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

		accordin.addTab(courseUIService.buildCoursePageOne(courseStepOne), "Create course			(1/2)");
		courseStepOne.getSaveCourse().setEnabled(false);
		courseUIService.attachEmptyValueListenerCourseStep1(courseStepOne);
		courseStepOne.getReset().addClickListener(click -> {
			DeletePopupView deleteView = new DeletePopupView();
			deleteView.show(getUI(), clickData -> {
				deleteView.setVisible(false);
				FactoryResponse fr = courseResponseService.deleteCourse(courseStepOne.getCourseName().getValue());
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

			this.courseModel = CourseModel.builder().courseName(courseStepOne.getCourseName().getOptionalValue().get())
					.description(courseStepOne.getCourseDescription().getOptionalValue().get())
					.isArchive(courseStepOne.getIsArchive().getValue())
					.maxStudentsAllowed(Long.valueOf(courseStepOne.getMaxStudents().getOptionalValue().get()))
					.totalSemestersInNumber(Long.valueOf(Utils.val(courseStepOne.getTotalSem()))).build();
			FactoryResponse data = courseResponseService.saveCourseMetadata(this.courseModel);

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
					accordin.setSelectedTab(1);
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
				internal = change.getValue().contains(SubjectType.INTERNAL.toString());
				theory = change.getValue().contains(SubjectType.THEORY.toString());
				practical = change.getValue().contains(SubjectType.PRACTICAL.toString());
				others = change.getValue().contains(SubjectType.OTHER.toString());
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

		courseStepTwo.getSaveCourse().addClickListener(click -> {
			if (!ListenerUtility.isValidSourceEvent(click.getComponent(), courseStepTwo.getSaveCourse())) {
				return;
			}
			if (this.courseModel == null) {
				Notification notifi = Notification.show("", Type.ERROR_MESSAGE);
				notifi.setDelayMsec(Notification.DELAY_FOREVER);
				notifi.setCaption("Error");
				notifi.setDescription(String.valueOf("Unable to save due to no course metadata available."));
				notifi.setIcon(VaadinIcons.STOP);
				return;
			}
			SubjectModel.SubjectModelBuilder subBuilder = SubjectModel.builder();
			if (this.subjects.contains(Utils.val(courseStepTwo.getSubjectName()))) {
				Notification notifi = Notification.show("", Type.ERROR_MESSAGE);
				notifi.setDelayMsec(Notification.DELAY_FOREVER);
				notifi.setCaption("Error");
				notifi.setDescription(String.valueOf("Subject already exists."));
				notifi.setIcon(VaadinIcons.STOP);
				return;
			}
			Set<String> items = courseStepTwo.getSubjectAttributes().getSelectedItems();

			if (CollectionUtils.isEmpty(items)) {
				Notification notifi = Notification.show("", Type.ERROR_MESSAGE);
				notifi.setDelayMsec(Notification.DELAY_FOREVER);
				notifi.setCaption("Error");
				notifi.setDescription(String.valueOf("No subject attribute is selected "));
				notifi.setIcon(VaadinIcons.STOP);
				return;
			}

			subBuilder.showInternal(items.contains("Internal"));
			subBuilder.internalMarksRequired(Utils.doubleVal(courseStepTwo.getInternalPassMarks()));
			subBuilder.internal(Utils.doubleVal(courseStepTwo.getInternalMarks()));

			subBuilder.showTheory(items.contains("Theory"));
			subBuilder.theory(Utils.doubleVal(courseStepTwo.getTheoryMarks()));
			subBuilder.theoryMarksRequired(Utils.doubleVal(courseStepTwo.getTheoryPassMarks()));

			subBuilder.showPractical(items.contains("Practical"));
			subBuilder.practical(Utils.doubleVal(courseStepTwo.getPracticalMarks()));
			subBuilder.practicalMarksRequired(Utils.doubleVal(courseStepTwo.getPracticalPassMarks()));

			subBuilder.showOthers(items.contains("Others"));
			subBuilder.others(Utils.doubleVal(courseStepTwo.getOtherMarks()));
			subBuilder.othersMarksRequired(Utils.doubleVal(courseStepTwo.getOtherPassMarks()));
			subBuilder.subjectName(Utils.val(courseStepTwo.getSubjectName()));
			subBuilder.semester(Utils.val(courseStepTwo.getCurrentSemester()));
			subjects.add(subBuilder.build().getSubjectName());
			subjectModels.add(subBuilder.build());
			courseStepTwo.getAddedSubjects().setItems(subjects);
		});
		courseStepTwo.getCompleteDialog().addClickListener(click -> {
			if (!ListenerUtility.isValidSourceEvent(click.getComponent(), courseStepTwo.getCompleteDialog())) {
				return;
			}
			if (this.courseModel == null) {
				Notification notifi = Notification.show("", Type.ERROR_MESSAGE);
				notifi.setDelayMsec(Notification.DELAY_FOREVER);
				notifi.setCaption("Error");
				notifi.setDescription(String.valueOf("Unable to save due to no course metadata available."));
				notifi.setIcon(VaadinIcons.STOP);
				return;
			}
			if (CollectionUtils.isEmpty(subjectModels)) {
				Notification notifi = Notification.show("", Type.ERROR_MESSAGE);
				notifi.setDelayMsec(Notification.DELAY_FOREVER);
				notifi.setCaption("Error");
				notifi.setDescription(String.valueOf("No subjects information provided."));
				notifi.setIcon(VaadinIcons.STOP);
				return;
			}
			this.courseModel.setSubjects(subjectModels);
			FactoryResponse fr = courseResponseService.createUpdateCourse(null, courseModel);
			Utils.showFactoryResponseMsg(fr);
		});
		VerticalLayout layout = courseUIService.buildCoursePageTwo(courseStepTwo);
		// layout.setSizeFull();
		accord.addTab(layout, "Create course			(2/2)");
		// accord.setSizeFull();
	}

	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		View.super.beforeLeave(event);
	}
}
