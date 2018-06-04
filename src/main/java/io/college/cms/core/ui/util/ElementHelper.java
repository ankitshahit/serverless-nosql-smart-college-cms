package io.college.cms.core.ui.util;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.shared.ui.ErrorLevel;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSingleSelect;

public class ElementHelper {
	public static void removeComponentError(AbstractField<?> field) {
		field.setComponentError(null);
	}

	public static void removeComponentError(AbstractSingleSelect<?> field) {
		field.setComponentError(null);
	}

	public static void addComponentError(AbstractField<?> field, String msg) {
		field.setComponentError(UIHelper.getErrorMessage(ErrorLevel.ERROR, msg));
	}

	public static void addComponentError(AbstractSingleSelect<?> field, String msg) {
		field.setComponentError(UIHelper.getErrorMessage(ErrorLevel.ERROR, msg));
	}

	public static boolean hasValue(AbstractField<?>... fields) {
		boolean hasValue = true;
		if (fields == null || !(fields.length > 0) || fields[0] == null) {
			return !hasValue;
		}
		for (AbstractField<?> field : fields) {
			if (StringUtils.isEmpty(String.valueOf(field.getValue()))) {
				hasValue = false;
				break;
			}
		}
		return hasValue;
	}

	public static boolean hasValue(AbstractSingleSelect<?>... fields) {
		boolean hasValue = true;
		if (fields == null || !(fields.length > 0) || fields[0] == null) {
			return !hasValue;
		}

		for (AbstractSingleSelect<?> field : fields) {
			if (!field.getSelectedItem().isPresent()) {
				hasValue = false;
			}
		}
		return hasValue;
	}

	public static <T> T value(Optional<T> value) {
		if (value.isPresent()) {
			return value.get();
		}
		return null;
	}
}
