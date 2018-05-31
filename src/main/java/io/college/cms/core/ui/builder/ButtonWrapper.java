package io.college.cms.core.ui.builder;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.server.Resource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ButtonWrapper {
	private String caption;
	private String description;
	private Resource icon;
	@Builder.Default()
	private ContentMode contentMode = ContentMode.TEXT;

	@Builder.Default()
	private boolean visible = true;
	@Builder.Default()
	private boolean enabled = true;
	@Builder.Default()
	private boolean required = true;
	private boolean captionAsHtml;
	private String height;
	private String width;

	private Button field;

	public Button button() {
		if (field == null) {
			field = new Button();
		}

		field.setVisible(visible);
		field.setEnabled(enabled);

		if (StringUtils.isNotEmpty(caption)) {
			field.setCaption(caption);
		}
		if (StringUtils.isNotEmpty(description)) {
			field.setDescription(description, contentMode);
		}
		if (icon != null) {
			field.setIcon(icon);
		}
		if (StringUtils.isNotEmpty(height)) {
			field.setHeight(height);
		}
		if (StringUtils.isNotEmpty(width)) {
			field.setWidth(width);
		}

		field.setCaptionAsHtml(captionAsHtml);
		return field;
	}
}
