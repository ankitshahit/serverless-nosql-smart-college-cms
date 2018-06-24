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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.IconGenerator;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.application.Utils;
import io.college.cms.core.courses.db.CourseModel;
import io.college.cms.core.courses.service.CourseResponseService;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CoreUiService {
	private CourseResponseService courseResponseService;

	@Autowired
	public void setCourseResponseService(CourseResponseService courseResponseService) {
		this.courseResponseService = courseResponseService;
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
	}
}
