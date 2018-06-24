package io.college.cms.core.admission.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.courses.db.CourseModel;
import io.college.cms.core.courses.service.CourseResponseService;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class ConfigureAdmissionActionView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;
	private CourseResponseService courseResponseService;
	private ConfigureAdmissionActionViewService configureAdmissionVewService;

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		try {
			FactoryResponse courseResponse = courseResponseService.findAllCourses(null, 0L, 0L);

			if (courseResponse == null || SummaryMessageEnum.SUCCESS != courseResponse.getSummaryMessage()) {
				Notification notifi = Notification.show("", Type.ERROR_MESSAGE);
				notifi.setIcon(VaadinIcons.STOP);
				notifi.setCaption("Error");
				notifi.setDescription("We couldn't load course data");
				notifi.setDelayMsec(Notification.DELAY_FOREVER);
				return;
			}
			List<CourseModel> models = null;
			models = (List<CourseModel>) courseResponse.getResponse();
			if (CollectionUtils.isEmpty(models)) {
				Notification notifi = Notification.show("", Type.ERROR_MESSAGE);
				notifi.setIcon(VaadinIcons.STOP);
				notifi.setCaption("Error");
				notifi.setDescription("We couldn't load course data");
				notifi.setDelayMsec(Notification.DELAY_FOREVER);
				return;
			}
			List<String> courseNames = new ArrayList<>();
			models.forEach(course -> {
				courseNames.add(course.getCourseName());
			});
			this.configureAdmissionVewService.getAdmissionDTO().getCoursesList().setItems(courseNames);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			Notification notifi = Notification.show("", Type.ERROR_MESSAGE);
			notifi.setCaption("Application error");
			notifi.setIcon(VaadinIcons.STOP_COG);
			notifi.setDescription(
					"We were unable to process request for some reason! Please try again later or contact admin");
			notifi.setDelayMsec(Notification.DELAY_FOREVER);
		} finally {

		}
	}

	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		View.super.beforeLeave(event);
	}

	@Data
	private static class ConfigureAdmissionActionViewService {
		private AdmissionServiceDTO admissionDTO;

		/**
		 * @param admissionDTO
		 */
		public ConfigureAdmissionActionViewService() {
			super();
			admissionDTO = AdmissionServiceDTO.builder().rootPanel(new Panel()).coursesList(new ComboBox<String>())
					.usersList(new ComboBox<String>()).addUserBtn(new Button()).addedUsersList(new ListSelect<String>())
					.saveBtn(new Button()).build();
		}

		protected void initVaadinPage() {
			initUI();
			initStyles();
			initLayout();
			initListener();
		}

		protected void initUI() {

		}

		protected void initStyles() {

		}

		protected void initLayout() {

		}

		protected void initListener() {

		}
	}

	@Data
	@Builder
	private static class AdmissionServiceDTO {
		private Panel rootPanel;
		private ComboBox<String> coursesList;
		private ComboBox<String> usersList;
		private Button addUserBtn;
		private ListSelect<String> addedUsersList;
		private Button saveBtn;
	}

}
