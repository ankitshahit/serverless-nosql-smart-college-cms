package io.college.cms.core.ui.builder;

import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.server.Resource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class VaadinWrapper {
	private String caption;
	private String description;
	private Resource icon;
	@Builder.Default()
	private ContentMode contentMode = ContentMode.HTML;
	private String placeholder;
	@Builder.Default()
	private boolean visible = true;
	@Builder.Default()
	private boolean enabled = true;
	@Builder.Default()
	private boolean required = true;
	@Builder.Default
	private boolean captionAsHtml = true;
	private String height;
	private String width;
	private int maxLength;
	private String value;
	private boolean readOnly;
	private TextField field;
	private ComboBox<?> comboBox;
	private DateField dateField;
	private RichTextArea richTextArea;
	private TextArea textArea;
	private Button button;
	private String style;
	private Label label;

	public TextField textField() {
		if (this.field == null) {
			this.field = new TextField();
		}
		this.field.setVisible(visible);
		this.field.setEnabled(enabled);
		if (maxLength > 0) {
			this.field.setMaxLength(maxLength);
		}
		if (StringUtils.isNotEmpty(caption)) {
			this.field.setCaption(new StringBuilder().append("<p><b>").append(StringUtils.capitalize(this.caption))
					.append("</b>: </p>").toString());
		}
		if (StringUtils.isNotEmpty(description)) {
			this.field.setDescription(description, contentMode);
		}
		if (icon != null) {
			this.field.setIcon(icon);
		}
		if (StringUtils.isNotEmpty(placeholder)) {
			this.field.setPlaceholder(StringUtils.capitalize(this.placeholder));
		}
		if (StringUtils.isNotEmpty(value)) {
			this.field.setValue(value);
		}
		if (StringUtils.isNotEmpty(height)) {
			this.field.setHeight(height);
		}
		if (StringUtils.isNotEmpty(width)) {
			this.field.setWidth(width);
		}
		if (StringUtils.isNotEmpty(style)) {
			this.field.setStyleName(style);
		}
		this.field.addStyleNames(ValoTheme.TEXTFIELD_ALIGN_CENTER, ValoTheme.TEXTFIELD_INLINE_ICON,
				ValoTheme.TEXTFIELD_LARGE);
		this.field.setCaptionAsHtml(captionAsHtml);
		this.field.setRequiredIndicatorVisible(required);
		this.field.setReadOnly(readOnly);
		this.field.setResponsive(true);
		this.field.setSizeFull();
		return this.field;
	}

	public ComboBox<?> comboBox() {
		if (this.comboBox == null) {
			this.comboBox = new ComboBox<>();
		}

		this.comboBox.setVisible(this.visible);
		this.comboBox.setEnabled(this.enabled);

		if (StringUtils.isNotEmpty(this.caption)) {
			this.comboBox.setCaption(new StringBuilder().append("<p><b>").append(StringUtils.capitalize(this.caption))
					.append("</b>: </p>").toString());
		}
		if (StringUtils.isNotEmpty(description)) {
			this.comboBox.setDescription(this.description, contentMode);
		}
		if (this.icon != null) {
			this.comboBox.setIcon(icon);
		}
		if (StringUtils.isNotEmpty(this.placeholder)) {
			this.comboBox.setPlaceholder(StringUtils.capitalize(this.placeholder));
		}
		if (StringUtils.isNotEmpty(height)) {
			this.comboBox.setHeight(height);
		}
		if (StringUtils.isNotEmpty(width)) {
			this.comboBox.setWidth(width);
		}
		if (StringUtils.isNotEmpty(style)) {
			this.comboBox.setStyleName(style);
		}
		this.comboBox.addStyleNames(ValoTheme.COMBOBOX_ALIGN_CENTER, ValoTheme.COMBOBOX_LARGE);
		this.comboBox.setCaptionAsHtml(captionAsHtml);
		this.comboBox.setRequiredIndicatorVisible(required);
		this.comboBox.setReadOnly(readOnly);
		this.comboBox.setResponsive(true);
		this.comboBox.setSizeFull();
		return this.comboBox;
	}

	public DateField dateField() {
		if (this.dateField == null) {
			this.dateField = new DateField();
		}

		this.dateField.setVisible(this.visible);
		this.dateField.setEnabled(this.enabled);

		if (StringUtils.isNotEmpty(this.caption)) {
			this.dateField.setCaption(new StringBuilder().append("<p><b>").append(StringUtils.capitalize(this.caption))
					.append("</b>: </p>").toString());
		}
		if (StringUtils.isNotEmpty(description)) {
			this.dateField.setDescription(this.description, contentMode);
		}
		if (this.icon != null) {
			this.dateField.setIcon(icon);
		}
		if (StringUtils.isNotEmpty(this.placeholder)) {
			this.dateField.setPlaceholder(StringUtils.capitalize(this.placeholder));
		}
		if (StringUtils.isNotEmpty(height)) {
			this.dateField.setHeight(height);
		}
		if (StringUtils.isNotEmpty(width)) {
			this.dateField.setWidth(width);
		}
		if (StringUtils.isNotEmpty(style)) {
			this.dateField.setStyleName(style);
		}
		this.dateField.addStyleNames(ValoTheme.DATEFIELD_ALIGN_CENTER, ValoTheme.DATEFIELD_LARGE);
		this.dateField.setCaptionAsHtml(captionAsHtml);
		this.dateField.setRequiredIndicatorVisible(required);
		this.dateField.setReadOnly(readOnly);
		this.dateField.setValue(LocalDate.now());
		this.dateField.setResponsive(true);
		this.dateField.setRangeStart(LocalDate.now());
		this.dateField.setSizeFull();
		return this.dateField;
	}

	public Button button() {
		if (this.button == null) {
			this.button = new Button();
		}

		this.button.setVisible(this.visible);
		this.button.setEnabled(this.enabled);

		if (StringUtils.isNotEmpty(this.caption)) {
			this.button.setCaption(new StringBuilder().append("<p><b>").append(StringUtils.capitalize(this.caption))
					.append("</b>: </p>").toString());
		}
		if (StringUtils.isNotEmpty(description)) {
			this.button.setDescription(this.description, contentMode);
		}
		if (this.icon != null) {
			this.button.setIcon(icon);
		}
		if (StringUtils.isNotEmpty(height)) {
			this.button.setHeight(height);
		}
		if (StringUtils.isNotEmpty(width)) {
			this.button.setWidth(width);
		}
		if (StringUtils.isNotEmpty(style)) {
			this.button.setStyleName(style);
		}
		this.button.addStyleNames(ValoTheme.BUTTON_FRIENDLY, ValoTheme.BUTTON_LARGE);
		this.button.setCaptionAsHtml(captionAsHtml);
		this.button.setResponsive(true);

		return this.button;
	}

	public Label label() {
		if (this.label == null) {
			this.label = new Label();
		}

		this.label.setVisible(this.visible);
		this.label.setEnabled(this.enabled);

		if (StringUtils.isNotEmpty(this.caption)) {
			this.label.setCaption(new StringBuilder().append("<p><b>").append(StringUtils.capitalize(this.caption))
					.append("</b>: </p>").toString());
		}
		if (StringUtils.isNotEmpty(description)) {
			this.label.setDescription(this.description, contentMode);
		}
		if (this.icon != null) {
			this.label.setIcon(icon);
		}
		if (StringUtils.isNotEmpty(height)) {
			this.label.setHeight(height);
		}
		if (StringUtils.isNotEmpty(width)) {
			this.label.setWidth(width);
		}
		if (StringUtils.isNotEmpty(style)) {
			this.label.setStyleName(style);
		}
		this.label.setContentMode(ContentMode.HTML);
		this.label.setCaptionAsHtml(captionAsHtml);
		this.label.setResponsive(true);
		return this.label;
	}

	public RichTextArea richTextArea() {
		if (this.richTextArea == null) {
			this.richTextArea = new RichTextArea();
		}
		this.richTextArea.setVisible(visible);
		this.richTextArea.setEnabled(enabled);
		if (StringUtils.isNotEmpty(caption)) {
			this.richTextArea.setCaption(new StringBuilder().append("<p><b>")
					.append(StringUtils.capitalize(this.caption)).append("</b>: </p>").toString());
		}
		if (StringUtils.isNotEmpty(description)) {
			this.richTextArea.setDescription(description, contentMode);
		}
		if (icon != null) {
			this.richTextArea.setIcon(icon);
		}
		if (StringUtils.isNotEmpty(value)) {
			this.richTextArea.setValue(value);
		}
		if (StringUtils.isNotEmpty(height)) {
			this.richTextArea.setHeight(height);
		}
		if (StringUtils.isNotEmpty(width)) {
			this.richTextArea.setWidth(width);
		}
		if (StringUtils.isNotEmpty(style)) {
			this.richTextArea.setStyleName(style);
		}
		this.richTextArea.addStyleNames(ValoTheme.TEXTAREA_ALIGN_CENTER, ValoTheme.TEXTAREA_LARGE);
		this.richTextArea.setCaptionAsHtml(captionAsHtml);
		this.richTextArea.setRequiredIndicatorVisible(required);
		this.richTextArea.setReadOnly(readOnly);
		this.richTextArea.setResponsive(true);
		this.richTextArea.setSizeFull();
		return this.richTextArea;
	}

	public TextArea textArea() {
		if (this.textArea == null) {
			this.textArea = new TextArea();
		}
		this.textArea.setVisible(visible);
		this.textArea.setEnabled(enabled);
		if (StringUtils.isNotEmpty(caption)) {
			this.textArea.setCaption(new StringBuilder().append("<p><b>").append(StringUtils.capitalize(this.caption))
					.append("</b>: </p>").toString());
		}
		if (StringUtils.isNotEmpty(description)) {
			this.textArea.setDescription(description, contentMode);
		}
		if (icon != null) {
			this.textArea.setIcon(icon);
		}
		if (StringUtils.isNotEmpty(value)) {
			this.textArea.setValue(value);
		}
		if (StringUtils.isNotEmpty(height)) {
			this.textArea.setHeight(height);
		}
		if (StringUtils.isNotEmpty(width)) {
			this.textArea.setWidth(width);
		}
		if (StringUtils.isNotEmpty(style)) {
			this.textArea.setStyleName(style);
		}
		this.textArea.addStyleNames(ValoTheme.TEXTAREA_ALIGN_CENTER, ValoTheme.TEXTAREA_LARGE);
		this.textArea.setCaptionAsHtml(captionAsHtml);
		this.textArea.setRequiredIndicatorVisible(required);
		this.textArea.setReadOnly(readOnly);
		this.textArea.setResponsive(true);
		this.textArea.setSizeFull();
		return this.textArea;
	}

	/**
	 * to set Mandatory TextFields
	 * 
	 * @param txt
	 * @param caption
	 * @param placeholder
	 */
	public static void setTextField(TextField txt, String caption, String placeholder) {
		txt.setCaption(new StringBuilder().append("<p><b>").append(StringUtils.capitalize(caption)).append("</b>: </p>")
				.toString());
		txt.setCaptionAsHtml(true);
		txt.setPlaceholder(StringUtils.capitalize(placeholder));
		txt.setVisible(true);
		txt.setEnabled(true);
		txt.setRequiredIndicatorVisible(true);
		txt.setResponsive(true);
		txt.addStyleNames(ValoTheme.TEXTFIELD_ALIGN_CENTER, ValoTheme.TEXTFIELD_INLINE_ICON, ValoTheme.TEXTFIELD_LARGE);
		txt.focus();
	}

	/**
	 * to set Mandatory TextFields
	 * 
	 * @param txt
	 * @param caption
	 * @param placeholder
	 */
	public static void setRichTextArea(RichTextArea txt, String caption, String placeholder) {
		txt.setCaption(new StringBuilder().append("<p><b>").append(StringUtils.capitalize(caption)).append("</b>: </p>")
				.toString());
		txt.setCaptionAsHtml(true);
		txt.setVisible(true);
		txt.setEnabled(true);
		txt.setRequiredIndicatorVisible(true);
		txt.setResponsive(true);
		txt.focus();
	}
}
