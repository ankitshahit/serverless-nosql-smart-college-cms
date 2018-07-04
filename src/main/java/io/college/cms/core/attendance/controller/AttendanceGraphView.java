package io.college.cms.core.attendance.controller;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.navigator.View;
import com.vaadin.ui.VerticalLayout;

import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class AttendanceGraphView extends VerticalLayout implements View {
	private Chart chart;

	@PostConstruct
	protected void paint() {
		chart = new Chart(ChartType.LINE);
		addComponent(chart);
	}
}
