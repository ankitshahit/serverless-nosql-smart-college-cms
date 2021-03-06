package io.college.cms.core.ui.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.IconGenerator;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.announcement.model.AnnouncementModel;
import io.college.cms.core.announcement.services.AnnouncementResponseService;
import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.application.Utils;
import io.college.cms.core.courses.db.CourseModel;
import io.college.cms.core.courses.service.CourseResponseService;
import io.college.cms.core.examination.model.ExaminationModel;
import io.college.cms.core.examination.model.TimeTableModel;
import io.college.cms.core.examination.service.ExamResponseService;
import io.college.cms.core.job.model.JobModel;
import io.college.cms.core.job.services.JobResponseService;
import io.college.cms.core.ui.builder.VaadinWrapper;
import io.college.cms.core.upload.model.UploadModel;
import io.college.cms.core.upload.services.UploadResponseService;
import io.college.cms.core.user.constants.UserGroups;
import io.college.cms.core.user.model.UserModel;
import io.college.cms.core.user.service.SecurityService;
import io.college.cms.core.user.service.UserResponseService;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CoreUiService {
	private CourseResponseService courseResponseService;
	private JobResponseService jobResponseService;
	private AnnouncementResponseService announcementResponseService;
	private UserResponseService userResponseService;
	private UploadResponseService uploadResponseService;
	@Autowired
	private ExamResponseService examResponseService;

	@Autowired
	public void setCourseResponseService(CourseResponseService courseResponseService) {
		this.courseResponseService = courseResponseService;
	}

	@Autowired
	public void setJobResponseService(JobResponseService jobResponseService) {
		this.jobResponseService = jobResponseService;
	}

	@Autowired
	public void setAnnouncementResponseService(AnnouncementResponseService announcementResponseService) {
		this.announcementResponseService = announcementResponseService;
	}

	@Autowired
	public void setUserResponseService(UserResponseService userResponseService) {
		this.userResponseService = userResponseService;
	}

	@Autowired
	public void setUploadResponseService(UploadResponseService uploadResponseService) {
		this.uploadResponseService = uploadResponseService;
	}

	public ComboBox<String> getCoursesList() {
		ComboBox<String> courses = new ComboBox<String>();
		courses.setCaption("Apply for course");
		courses.setPlaceholder("Select course");
		courses.setRequiredIndicatorVisible(true);
		courses.setVisible(true);
		courses.setEnabled(true);
		courses.addStyleNames(ValoTheme.COMBOBOX_LARGE, ValoTheme.COMBOBOX_ALIGN_CENTER);
		courses.setItemIconGenerator(new IconGenerator<String>() {
			@Override
			public Resource apply(String item) {
				return VaadinIcons.SITEMAP;
			}
		});
		return courses;
	}

	public void setExamSubjects(ComboBox<String> subjectsCb, String examName) {
		FactoryResponse fr = examResponseService.findTimeTable(examName);
		if (Utils.isError(fr)) {
			return;
		}
		List<TimeTableModel> examModels = (List<TimeTableModel>) fr.getResponse();
		if (CollectionUtils.isEmpty(examModels)) {
			return;
		}
		List<String> subjectNames = new ArrayList<>();

		examModels.forEach(exam -> {
			subjectNames.add(exam.getSubject());
		});
		subjectsCb.setItems(subjectNames);
	}

	public void setExamsName(ComboBox<String> examsName) {
		FactoryResponse fr = examResponseService.findAllExams();
		if (Utils.isError(fr)) {
			return;
		}
		List<ExaminationModel> examModels = (List<ExaminationModel>) fr.getResponse();
		if (CollectionUtils.isEmpty(examModels)) {
			return;
		}
		List<String> examNames = new ArrayList<>();

		examModels.forEach(exam -> {
			examNames.add(exam.getExamName());
		});
		examsName.setItems(examNames);
	}

	public ComboBox<String> getSemesterList() {
		ComboBox<String> semester = new ComboBox<String>();
		semester.setCaption("Select semester");
		semester.setPlaceholder("Select semester");
		semester.setRequiredIndicatorVisible(true);
		semester.setVisible(true);
		semester.setEnabled(true);
		semester.addStyleNames(ValoTheme.COMBOBOX_LARGE, ValoTheme.COMBOBOX_ALIGN_CENTER);
		semester.setItemIconGenerator(new IconGenerator<String>() {
			@Override
			public Resource apply(String item) {
				return VaadinIcons.SITEMAP;
			}
		});
		return semester;
	}

	public ComboBox<String> getSubjectList() {
		ComboBox<String> subject = new ComboBox<String>();
		subject.setCaption("Select Subject: ");
		subject.setPlaceholder("Select by entering subject name");
		subject.setVisible(true);
		subject.setEnabled(true);
		subject.addStyleNames(ValoTheme.COMBOBOX_LARGE, ValoTheme.COMBOBOX_ALIGN_CENTER);
		subject.setItemIconGenerator(new IconGenerator<String>() {
			@Override
			public Resource apply(String item) {
				return VaadinIcons.SITEMAP;
			}
		});
		return subject;
	}

	public void setItemsCourseNames(ComboBox<String> courses) {
		List<CourseModel> models = null;
		List<String> courseNames = new ArrayList<>();

		FactoryResponse courseResponse = courseResponseService.findAllCourses(null, 0L, 0L);

		if (courseResponse == null || SummaryMessageEnum.SUCCESS != courseResponse.getSummaryMessage()) {
			Notification notifi = Notification.show("", Type.ERROR_MESSAGE);
			notifi.setIcon(VaadinIcons.STOP);
			notifi.setCaption("Error");
			notifi.setDescription("We couldn't load course data");
			notifi.setDelayMsec(Notification.DELAY_FOREVER);
			return;
		}
		models = (List<CourseModel>) courseResponse.getResponse();
		if (CollectionUtils.isEmpty(models)) {
			Notification notifi = Notification.show("", Type.ERROR_MESSAGE);
			notifi.setIcon(VaadinIcons.STOP);
			notifi.setCaption("Error");
			notifi.setDescription("We couldn't load course data");
			notifi.setDelayMsec(Notification.DELAY_FOREVER);
			return;
		}
		models.forEach(course -> {
			courseNames.add(course.getCourseName());
		});
		courses.setItems(courseNames);
	}

	public void setItemsSemester(ComboBox<String> semesterCb, String courseName) {
		CourseModel course = null;
		List<String> semesters = new ArrayList<>();
		FactoryResponse fr = courseResponseService.findByCourseName(null, courseName);
		Utils.showFactoryResponseOnlyError(fr);
		if (Utils.isError(fr)) {
			return;
		}
		course = (CourseModel) fr.getResponse();
		if (Utils.isNull(course)) {
			return;
		}
		if (CollectionUtils.isEmpty(course.getSemesters())) {
			semesters.add(CourseModel.SubjectModel.DEFAULT_SEM);
		} else {
			semesters.addAll(course.getSemesters());
		}
		semesterCb.setItems(semesters);
	}

	public void setItemsSubject(ComboBox<String> subjectCb, String courseName, String semester) {
		FactoryResponse fr = courseResponseService.findByCourseName(null, courseName);
		CourseModel course = null;
		List<String> subjects = new ArrayList<>();

		Utils.showFactoryResponseOnlyError(fr);
		if (Utils.isError(fr)) {
			return;
		}
		course = (CourseModel) fr.getResponse();
		if (Utils.isNull(course)) {
			return;
		}

		if (!(CollectionUtils.isEmpty(course.getSemesters())
				&& CourseModel.SubjectModel.DEFAULT_SEM.equalsIgnoreCase(semester))) {
			Utils.showErrorNotification("No such semester available.");
		}
		if (CollectionUtils.isEmpty(course.getSubjects())) {
			subjectCb.setItems("No items");
		}
		for (CourseModel.SubjectModel subjectModel : course.getSubjects()) {
			if (!semester.equalsIgnoreCase(subjectModel.getSemester())) {
				continue;
			}
			subjects.add(subjectModel.getSubjectName());
		}
		subjectCb.setItems(subjects);
	}

	public List<CourseModel.SubjectModel> getSubjectsModel(String courseName, String semester) {
		FactoryResponse fr = courseResponseService.findByCourseName(null, courseName);
		CourseModel course = null;
		List<CourseModel.SubjectModel> subjects = new ArrayList<>();

		Utils.showFactoryResponseOnlyError(fr);
		if (Utils.isError(fr)) {
			return subjects;
		}
		course = (CourseModel) fr.getResponse();
		if (Utils.isNull(course)) {
			return subjects;
		}

		if (!(CollectionUtils.isEmpty(course.getSemesters())
				&& CourseModel.SubjectModel.DEFAULT_SEM.equalsIgnoreCase(semester))) {
			Utils.showErrorNotification("No such semester available.");
		}
		for (CourseModel.SubjectModel subjectModel : course.getSubjects()) {
			if (!semester.equalsIgnoreCase(subjectModel.getSemester())) {
				continue;
			}
			subjects.add(subjectModel);
		}
		return subjects;
	}

	public void setItemsUser(ComboBox<String> grid) {
		FactoryResponse fr = this.userResponseService.getUsers(null, UserModel.builder().build());
		List<UserModel> users = null;
		Utils.showFactoryResponseOnlyError(fr);
		if (Utils.isError(fr)) {
			return;
		}
		users = (List<UserModel>) fr.getResponse();
		if (CollectionUtils.isEmpty(users)) {
			return;
		}
		List<String> names = new ArrayList<>();
		users.forEach(user -> names.add(user.getUsername()));
		grid.setItems(names);
	}

	public void setItemsJob(Grid<JobModel> grid) {
		FactoryResponse fr = jobResponseService.findAllJobs();
		List<JobModel> jobs = null;

		Utils.showFactoryResponseOnlyError(fr);
		if (Utils.isError(fr)) {
			return;
		}
		jobs = (List<JobModel>) fr.getResponse();
		if (CollectionUtils.isEmpty(jobs)) {
			return;
		}
		grid.setItems(jobs);
	}

	public void setItemsAnnouncement(Grid<AnnouncementModel> grid) {
		FactoryResponse fr = this.announcementResponseService.findAllJobs();
		List<AnnouncementModel> announcements = null;
		Utils.showFactoryResponseOnlyError(fr);
		if (Utils.isError(fr)) {
			return;
		}
		announcements = (List<AnnouncementModel>) fr.getResponse();
		if (CollectionUtils.isEmpty(announcements)) {
			return;
		}
		grid.setItems(announcements);
	}

	public void setItemsUser(Grid<UserModel> grid) {
		FactoryResponse fr = this.userResponseService.getUsers(null, UserModel.builder().build());
		List<UserModel> users = null;
		Utils.showFactoryResponseOnlyError(fr);
		if (Utils.isError(fr)) {
			return;
		}
		users = (List<UserModel>) fr.getResponse();
		if (CollectionUtils.isEmpty(users)) {
			return;
		}
		grid.setItems(users);
	}

	public void setItemsUpload(Grid<UploadModel> grid, String username, UserGroups groups) {
		if (SecurityService.ANONYMOUS_USER.equalsIgnoreCase(username)) {
			return;
		}
		FactoryResponse fr = this.uploadResponseService.findAll(username, groups);
		List<UploadModel> users = null;
		Utils.showFactoryResponseOnlyError(fr);
		if (Utils.isError(fr)) {
			return;
		}
		users = (List<UploadModel>) fr.getResponse();
		if (CollectionUtils.isEmpty(users)) {
			return;
		}
		grid.setItems(users);
	}

	public Label getLabel() {
		Label label = new Label();
		label.setCaptionAsHtml(true);
		label.setContentMode(ContentMode.HTML);
		return label;
	}

	public Label getLabel(String caption, String description) {
		Label label = getLabel();
		label.setCaption(caption);
		label.setDescription(description);
		return label;
	}

	public Label getLabel(String caption) {
		Label label = getLabel();
		label.setCaption(caption);
		return label;
	}

	public TextField getFees() {
		TextField fees = VaadinWrapper.builder().caption("Provide details about fees <br/>to be shown to student.")
				.build().textField();
		fees.setValue("Rs. ");
		fees.removeStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
		return fees;
	}
}
