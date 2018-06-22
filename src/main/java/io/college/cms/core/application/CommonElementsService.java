package io.college.cms.core.application;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.ui.ComboBox;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CommonElementsService {
	public ComboBox<String> getCourseNames() {
		return null;
	}

	public ComboBox<String> getSemesterNames(ComboBox<String> semester) {
		return null;
	}

}
