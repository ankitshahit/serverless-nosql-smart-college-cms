package io.college.cms.core.ui.util;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSingleSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;

public class ListenerUtility {
	public static final String FIELD_EMPTY = "Validation ERROR: Field cannot be empty.";

	public static boolean isValidSourceEvent(Component comp, Component button) {
		return comp != null && button != null && comp.getConnectorId().equalsIgnoreCase(button.getConnectorId());
	}

	public static void navigateToListener(UI ui, Button button, String view) {
		button.addClickListener(clickEvent -> {
			if (isValidSourceEvent(clickEvent.getComponent(), button)) {
				ui.getNavigator().navigateTo(view);
			}
		});
	}

	public static void emptyValueListener(AbstractField<?> field) {
		String currentValue = String.valueOf(field.getValue());
		if (StringUtils.isEmpty(currentValue)) {
			ElementHelper.addComponentError(field, ListenerUtility.FIELD_EMPTY);

		} else {
			ElementHelper.removeComponentError(field);

		}
	}

	public static void emptyValueListener(AbstractSingleSelect<?> field) {

		if (!field.getOptionalValue().isPresent()) {
			ElementHelper.addComponentError(field, ListenerUtility.FIELD_EMPTY);
		} else {
			ElementHelper.removeComponentError(field);

		}
	}
}
