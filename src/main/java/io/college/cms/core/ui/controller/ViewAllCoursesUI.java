package io.college.cms.core.ui.controller;

import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

import io.college.cms.core.ui.services.ICoursesService;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ViewAllCoursesUI implements View, ICoursesService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -414888885338170619L;

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
	}

	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		View.super.beforeLeave(event);
	}

}
