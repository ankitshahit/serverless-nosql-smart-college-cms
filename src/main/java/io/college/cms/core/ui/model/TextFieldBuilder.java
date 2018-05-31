package io.college.cms.core.ui.model;

import com.vaadin.server.Resource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.TextField;

import lombok.Data;

@Data
public class TextFieldBuilder {
	private String caption;
	private boolean captionAsHtml;
	private boolean enabled;
	private String placeholder;
	private String description;
	private ContentMode contentMode;
	private String height;
	private String width;
	private Resource icon;
	private boolean requiredVisible;
	private TextField field;
	private static TextFieldBuilder builder;

	public TextFieldBuilder() {
		this.field = new TextField();
		this.field.setVisible(true);
	}

	public static TextFieldBuilder builder() {
		builder = new TextFieldBuilder();
		return builder;
	}

	public static TextFieldBuilder caption(String caption) {
		builder.caption = caption;
		return builder;
	}

	public static TextFieldBuilder captionAsHtml(boolean asHtml) {
		builder.captionAsHtml = asHtml;
		return builder;
	}

	public static TextFieldBuilder requiredVisible(boolean required) {
		builder.captionAsHtml = required;
		return builder;
	}

	public static TextFieldBuilder enabled(boolean required) {
		builder.enabled = required;
		return builder;
	}

	public static TextFieldBuilder placeholder(String required) {
		builder.placeholder = required;
		return builder;
	}

	public static TextFieldBuilder description(String required) {
		builder.description = required;
		return builder;
	}

	public TextFieldBuilder contentMode(ContentMode required) {
		builder.contentMode = required;
		return builder;
	}

	public static TextFieldBuilder height(String required) {
		builder.height = required;
		return builder;
	}

	public static TextFieldBuilder width(String required) {
		builder.width = required;
		return builder;
	}

	public TextFieldBuilder icon(Resource required) {
		builder.icon = required;
		return builder;
	}

	public TextField build() {
		field.setCaption(caption);
		field.setCaptionAsHtml(captionAsHtml);
		field.setDescription(description);
		field.setHeight(height);
		field.setWidth(width);
		field.setIcon(icon);
		field.setRequiredIndicatorVisible(requiredVisible);
		field.setPlaceholder(placeholder);
		return field;
	}
}
