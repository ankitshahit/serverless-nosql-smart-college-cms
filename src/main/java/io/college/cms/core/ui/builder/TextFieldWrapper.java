package io.college.cms.core.ui.builder;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.server.Resource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.TextField;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TextFieldWrapper {
	private String caption;
	private String description;
	private Resource icon;
	@Builder.Default()
	private ContentMode contentMode = ContentMode.TEXT;
	private String placeholder;
	@Builder.Default()
	private boolean visible = true;
	@Builder.Default()
	private boolean enabled = true;
	@Builder.Default()
	private boolean required = true;
	private boolean captionAsHtml;
	private String height;
	private String width;
	private int maxLength;
	private String value;
	private boolean readOnly;
	private TextField field;
	private String style;

	public TextField textField() {
		if (field == null) {
			field = new TextField();
		}

		field.setVisible(visible);
		field.setEnabled(enabled);
		if (maxLength > 0) {
			field.setMaxLength(maxLength);
		}
		if (StringUtils.isNotEmpty(caption)) {
			field.setCaption(caption);
		}
		if (StringUtils.isNotEmpty(description)) {
			field.setDescription(description, contentMode);
		}
		if (icon != null) {
			field.setIcon(icon);
		}
		if (StringUtils.isNotEmpty(placeholder)) {
			field.setPlaceholder(placeholder);
		}
		if (StringUtils.isNotEmpty(value)) {
			field.setValue(value);
		}
		if (StringUtils.isNotEmpty(height)) {
			field.setHeight(height);
		}
		if (StringUtils.isNotEmpty(width)) {
			field.setWidth(width);
		}
		if (StringUtils.isNotEmpty(style)) {
			field.setStyleName(style);
		}
		field.setCaptionAsHtml(captionAsHtml);
		field.setRequiredIndicatorVisible(required);
		field.setReadOnly(readOnly);
		return field;
	}

	public static TextField setAttributes(TextField textField, String caption, String placeholder, String description,
			Resource icon, boolean isRequired) {
		
		textField.setCaption(caption);
		textField.setPlaceholder(placeholder);
		textField.setDescription(description);
		textField.setIcon(icon);
		textField.setRequiredIndicatorVisible(isRequired);
		return textField;
	}
}
