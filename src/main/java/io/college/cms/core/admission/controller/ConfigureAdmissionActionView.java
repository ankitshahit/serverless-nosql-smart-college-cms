package io.college.cms.core.admission.controller;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import lombok.Builder;
import lombok.Data;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ConfigureAdmissionActionView extends VerticalLayout implements View {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
	}

	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		View.super.beforeLeave(event);
	}

	static class ConfigureAdmissionActionViewService {

	}

	@Data
	@Builder
	static class AdmissionServiceDTO {
		private Panel rootPanel;
		private ComboBox<String> coursesList;
		private ComboBox<String> usersList;
		private Button addUserBtn;
		private ListSelect<String> addedUsersList;
		private Button saveBtn;
	}

}
