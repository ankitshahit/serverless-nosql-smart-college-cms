package io.college.cms.core.ui.util;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.ErrorMessage;
import com.vaadin.shared.ui.ErrorLevel;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;

import io.college.cms.core.ui.model.ViewConstants;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class UIHelper {
	public MenuBar getMenuBar(Navigator navigator) {
		MenuBar menu = new MenuBar();
		menu.addItem("", (MenuBar.Command) menuItem -> navigator.navigateTo("")).setIcon(VaadinIcons.HOME);
		MenuItem student = menu.addItem("", null);
		student.setIcon(VaadinIcons.ACADEMY_CAP);
		MenuItem courses = menu.addItem("", null);
		courses.setIcon(VaadinIcons.ABSOLUTE_POSITION);
		courses.addItem("create course", (MenuBar.Command) action -> {
			navigator.navigateTo(ViewConstants.COURSES_CREATE);
		});
		courses.addItem("delete course", (MenuBar.Command) action -> {
			navigator.navigateTo(ViewConstants.COURSES_DELETE);
		});

		return menu;
	}

	public static ErrorMessage getErrorMessage(ErrorLevel errorLevel, String msg) {
		return new ErrorMessage() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public ErrorLevel getErrorLevel() {
				return errorLevel;
			}

			@Override
			public String getFormattedHtmlMessage() {
				return msg;
			}
		};
	}

}
