package io.college.cms.core.application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.ui.Alignment;

import lombok.Cleanup;

public class VaadinFieldInitializer {
	static List<String> declarations = new ArrayList<>();
	static List<String> methods = new ArrayList<>();

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {

		@Cleanup
		Scanner sc = new Scanner(new File("F:\\workspace\\college.io\\src\\main\\resources\\see_results.json"));
		StringBuilder sb = new StringBuilder();
		while (sc.hasNextLine())
			sb.append(sc.nextLine());
		ObjectMapper objectMapper = new ObjectMapper();
		FieldModel fieldModel = objectMapper.readValue(sb.toString(), FieldModel.class);
		Map<String, Field> nameMappings = new HashMap<>();
		Map<String, Field> requiredFields = new HashMap<>();
		StringBuilder mandatoryFieldsSingle = new StringBuilder();
		StringBuilder mandatoryFieldsList = new StringBuilder();
		StringBuilder lines = new StringBuilder();

		for (Field field : fieldModel.getFields()) {
			FieldType element = FieldType.findByType(field.getType());
			nameMappings.put(field.getName(), field);
			if (field.isRequired()) {
				requiredFields.put(field.getName(), field);
				if (!(FieldType.VL == element || FieldType.HL == element || FieldType.PL == element
						|| FieldType.BTN == element)) {
					if (FieldType.CB == element) {
						mandatoryFieldsList.append(field.getName()).append(",");
					} else {
						mandatoryFieldsSingle.append(field.getName()).append(",");
					}
				}

			}
			addDeclaration(field, element);
			if (!(FieldType.VL == element || FieldType.HL == element || FieldType.PL == element)) {
				addMethods(field, element);
			}
			addComponent(field, element);
			addAlignment(field, element);

		}

		for (String string : declarations) {

			lines.append(string);
		}
		for (String string : methods) {
			lines.append(string);
		}
		for (String key : requiredFields.keySet()) {
			Field field = requiredFields.get(key);
			if (CollectionUtils.isEmpty(field.getListeners())) {
				continue;
			}
			for (Listener listener : field.getListeners()) {
				lines.append(createListener(field, listener, mandatoryFieldsSingle.toString(),
						mandatoryFieldsList.toString()));
			}
		}

		lines.insert(0, "@PostConstruct public void paint(){");
		lines.append("}");
		System.out.println(lines.toString());
	}

	public static void addDeclaration(Field field, FieldType fieldType) {

		declarations.add(new StringBuilder().append(fieldType.className()).append(" ").append(field.getName())
				.append(" = ").append(fieldType.constructor()).append("; ").toString());
	}

	public static void addMethods(Field field, FieldType fieldType) {
		StringBuilder sb = new StringBuilder();

		sb.setLength(0);
		if (StringUtils.isNotEmpty(field.getCaption())) {
			sb.append(field.getName()).append(".setCaption(\"").append(field.getCaption()).append("\");");
		}
		if (StringUtils.isNotEmpty(field.getPlaceholder())) {
			sb.append(field.getName()).append(".setPlaceholder(\"").append(field.getPlaceholder()).append("\");");
		}
		if (StringUtils.isNotEmpty(field.getIcon())) {
			sb.append(field.getName()).append(".setIcon(").append(field.getIcon()).append(");");
		}
		if (StringUtils.isNotEmpty(field.getHeight())) {
			sb.append(field.getName()).append(".setHeight(\"").append("").append(field.getHeight()).append("\");");
		}
		if (StringUtils.isNotEmpty(field.getWidth())) {
			sb.append(field.getName()).append(".setWidth(\"").append("").append(field.getHeight()).append("\");");
		}
		if (field.isRequired()) {
			sb.append(field.getName()).append(".setRequiredIndicatorVisible(").append("").append(field.isRequired())
					.append(");");
		}
		sb.append(field.getName()).append(".setVisible(").append("").append(field.isVisible()).append(");");
		sb.append(field.getName()).append(".setEnabled(").append("").append(field.isEnabled()).append(");");
		if (field.getMaxLength() > 0) {
			sb.append(field.getName()).append(".setMaxLength(").append(field.getMaxLength()).append(");");
		}

		methods.add(sb.append("\n").toString());
	}

	public static void addComponent(Field field, FieldType fieldType) {
		StringBuilder sb = new StringBuilder();
		if (StringUtils.isNotEmpty(field.getAddToComponent())) {
			sb.append(field.getAddToComponent()).append(".");
		}
		sb.append(field.getAddToComponentMethodName()).append("(").append(field.getName()).append(");");
		methods.add(sb.toString());
	}

	public static void addAlignment(Field field, FieldType type) {
		StringBuilder sb = new StringBuilder();
		if (field.getAlignment() == null) {
			return;
		}
		if (StringUtils.isNotEmpty(field.getAlignmentComponentName())) {
			sb.append(field.getAlignmentComponentName()).append(".");
		}
		if (StringUtils.isNotEmpty(field.getAlignmentMethodName())) {
			sb.append(field.getAlignmentMethodName()).append("(").append(field.getName()).append(",")
					.append(field.getAlignment()).append(");");
		}
		methods.add(sb.toString());
	}

	public static String createListener(Field field, Listener listener, String mandatoryFields,
			String mandatoryListFields) {
		StringBuilder sb = new StringBuilder();
		ListenerType listenerType = ListenerType.findByType(listener.getType());
		FieldType type = FieldType.findByType(field.getType());

		if (type == FieldType.DF) {
			sb.append(String.format(listenerType.className(), "LocalDate")).append(" ").append(field.getName())
					.append("Listener").append(" = ").append(String.format(listenerType.constructor(), "LocalDate"))
					.append(";");
		} else {
			sb.append(String.format(listenerType.className(), "String")).append(" ").append(field.getName())
					.append("Listener").append(" = ").append(String.format(listenerType.constructor(), "String"))
					.append(";");
		}
		if (listenerType != ListenerType.CV) {
			if (type == FieldType.CB) {
				sb.append(field.getName()).append("Listener").append(".setSourceListField(").append(field.getName())
						.append(");");

			} else if (type == FieldType.DF) {
				sb.append(field.getName()).append("Listener").append(".setSourceDateField(").append(field.getName())
						.append(");");
			} else {
				sb.append(field.getName()).append("Listener").append(".setSourceField(").append(field.getName())
						.append(");");

			}
		}
		sb.append(field.getName()).append("Listener").append(".setTargetBtn(").append(listener.getTargetbtn())
				.append(");");
		if (type == FieldType.DF) {
			sb.append(field.getName()).append("Listener").append(".setMandatoryDateFields(").append(field.getName())
					.append(");");
		}
		if (!mandatoryFields.isEmpty()) {
			sb.append(field.getName()).append("Listener").append(".setMandatoryFields(")
					.append(mandatoryFields.substring(0, mandatoryFields.length() - 1)).append(");");
		}
		if (!mandatoryListFields.isEmpty()) {
			sb.append(field.getName()).append("Listener").append(".setMandatoryListFields(")
					.append(mandatoryListFields.substring(0, mandatoryListFields.length() - 1)).append(");");
		}
		if (listenerType == ListenerType.CV) {
			sb.append(field.getName()).append(".addClickListener(").append(field.getName()).append("Listener")
					.append(");");
		} else {
			sb.append(field.getName()).append(".addValueChangeListener(").append(field.getName()).append("Listener")
					.append(");");
		}

		return sb.append("\n").toString();
	}

	public enum ListenerType {
		EL("EmptyFieldListener<%s>", "new EmptyFieldListener<%s>()"), CL("", ""), SH("ShowHideListener<%s>",
				"new ShowHideListener<%s>()"), CV("ClearValueListener<%s>", "new ClearValueListener<%s>()");
		private String className;
		private String constructor;

		ListenerType(String className, String constructor) {
			this.className = className;
			this.constructor = constructor;
		}

		public String className() {
			return className;
		}

		public String constructor() {
			return constructor;
		}

		public static ListenerType findByType(String val) {
			for (ListenerType type : values()) {
				if (type.toString().equalsIgnoreCase(val)) {
					return type;
				}
			}
			throw new EnumConstantNotPresentException(ListenerType.class, "NO such constant found for type");
		}
	}

	public enum FieldType {
		TF("TextField", "new TextField()"), TA("TextArea", "new TextArea()"), BTN("Button", "new Button()"), LB("Label",
				"new Label()"), HL("HorizontalLayout", "new HorizontalLayout()"), VL("VerticalLayout",
						"new VerticalLayout()"), CK("CheckBox", "new CheckBox()"), CBG("CheckBoxGroup<String>",
								"new CheckBoxGroup<String>()"), CB("ComboBox<String>", "new ComboBox<String>()"), PL(
										"Panel", "new Panel()"), DF("DateField", "new DateField()"), RTA("RichTextArea",
												"new RichTextArea()"), GD("Grid<String>", "new Grid<>()");
		private String className;
		private String constructor;

		FieldType(String className, String constructor) {
			this.className = className;
			this.constructor = constructor;
		}

		public String className() {
			return className;
		}

		public String constructor() {
			return constructor;
		}

		public static FieldType findByType(String val) {
			for (FieldType type : values()) {
				if (type.toString().equalsIgnoreCase(val)) {
					return type;
				}
			}
			throw new EnumConstantNotPresentException(FieldType.class, "NO such constant found for " + val);
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({ "type", "name", "caption", "placeholder", "icon", "visible", "enabled", "required", "height",
			"width", "listeners" })
	public static class Field {
		@JsonProperty("max_length")
		private int maxLength = 0;
		@JsonProperty("type")
		private String type;
		@JsonProperty("name")
		private String name;
		@JsonProperty("caption")
		private String caption;
		@JsonProperty("placeholder")
		private String placeholder;
		@JsonProperty("icon")
		private String icon;
		@JsonProperty("visible")
		private boolean visible = true;
		@JsonProperty("enabled")
		private boolean enabled = true;
		@JsonProperty("required")
		private boolean required = true;
		@JsonProperty("height")
		private String height;
		@JsonProperty("width")
		private String width;
		@JsonProperty("add_to_component")
		private String addToComponent;
		@JsonProperty("add_to_component_method")
		private String addToComponentMethodName;
		@JsonProperty("alignment")
		private String alignment;
		@JsonProperty("alignment_component_name")
		private String alignmentComponentName;
		@JsonProperty("alignment_method_name")
		private String alignmentMethodName;
		@JsonProperty("listeners")
		private List<Listener> listeners = null;
		@JsonIgnore
		private Map<String, Object> additionalProperties = new HashMap<String, Object>();

		public String getAlignment() {
			return alignment;
		}

		public void setAlignment(String alignment) {
			this.alignment = alignment;
		}

		public String getAlignmentComponentName() {
			return alignmentComponentName;
		}

		public void setAlignmentComponentName(String alignmentComponentName) {
			this.alignmentComponentName = alignmentComponentName;
		}

		public String getAlignmentMethodName() {
			return alignmentMethodName;
		}

		public void setAlignmentMethodName(String alignmentMethodName) {
			this.alignmentMethodName = alignmentMethodName;
		}

		public int getMaxLength() {
			return maxLength;
		}

		public void setMaxLength(int maxLength) {
			this.maxLength = maxLength;
		}

		public String getAddToComponentMethodName() {
			return addToComponentMethodName;
		}

		public void setAddToComponentMethodName(String addToComponentMethodName) {
			this.addToComponentMethodName = addToComponentMethodName;
		}

		public String getAddToComponent() {
			return addToComponent;
		}

		public void setAddToComponent(String addToComponent) {
			this.addToComponent = addToComponent;
		}

		public void setAdditionalProperties(Map<String, Object> additionalProperties) {
			this.additionalProperties = additionalProperties;
		}

		@JsonProperty("type")
		public String getType() {
			return type;
		}

		@JsonProperty("type")
		public void setType(String type) {
			this.type = type;
		}

		public Field withType(String type) {
			this.type = type;
			return this;
		}

		@JsonProperty("name")
		public String getName() {
			return name;
		}

		@JsonProperty("name")
		public void setName(String name) {
			this.name = name;
		}

		public Field withName(String name) {
			this.name = name;
			return this;
		}

		@JsonProperty("caption")
		public String getCaption() {
			return caption;
		}

		@JsonProperty("caption")
		public void setCaption(String caption) {
			this.caption = caption;
		}

		public Field withCaption(String caption) {
			this.caption = caption;
			return this;
		}

		@JsonProperty("placeholder")
		public String getPlaceholder() {
			return placeholder;
		}

		@JsonProperty("placeholder")
		public void setPlaceholder(String placeholder) {
			this.placeholder = placeholder;
		}

		public Field withPlaceholder(String placeholder) {
			this.placeholder = placeholder;
			return this;
		}

		@JsonProperty("icon")
		public String getIcon() {
			return icon;
		}

		@JsonProperty("icon")
		public void setIcon(String icon) {
			this.icon = icon;
		}

		public Field withIcon(String icon) {
			this.icon = icon;
			return this;
		}

		@JsonProperty("visible")
		public boolean isVisible() {
			return visible;
		}

		@JsonProperty("visible")
		public void setVisible(boolean visible) {
			this.visible = visible;
		}

		public Field withVisible(boolean visible) {
			this.visible = visible;
			return this;
		}

		@JsonProperty("enabled")
		public boolean isEnabled() {
			return enabled;
		}

		@JsonProperty("enabled")
		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public Field withEnabled(boolean enabled) {
			this.enabled = enabled;
			return this;
		}

		@JsonProperty("required")
		public boolean isRequired() {
			return required;
		}

		@JsonProperty("required")
		public void setRequired(boolean required) {
			this.required = required;
		}

		public Field withRequired(boolean required) {
			this.required = required;
			return this;
		}

		@JsonProperty("height")
		public String getHeight() {
			return height;
		}

		@JsonProperty("height")
		public void setHeight(String height) {
			this.height = height;
		}

		public Field withHeight(String height) {
			this.height = height;
			return this;
		}

		@JsonProperty("width")
		public String getWidth() {
			return width;
		}

		@JsonProperty("width")
		public void setWidth(String width) {
			this.width = width;
		}

		public Field withWidth(String width) {
			this.width = width;
			return this;
		}

		@JsonProperty("listeners")
		public List<Listener> getListeners() {
			return listeners;
		}

		@JsonProperty("listeners")
		public void setListeners(List<Listener> listeners) {
			this.listeners = listeners;
		}

		public Field withListeners(List<Listener> listeners) {
			this.listeners = listeners;
			return this;
		}

		@JsonAnyGetter
		public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		@JsonAnySetter
		public void setAdditionalProperty(String name, Object value) {
			this.additionalProperties.put(name, value);
		}

		public Field withAdditionalProperty(String name, Object value) {
			this.additionalProperties.put(name, value);
			return this;
		}

	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({ "fields" })
	public static class FieldModel {

		@JsonProperty("fields")
		private List<Field> fields = null;
		@JsonIgnore
		private Map<String, Object> additionalProperties = new HashMap<String, Object>();

		@JsonProperty("fields")
		public List<Field> getFields() {
			return fields;
		}

		@JsonProperty("fields")
		public void setFields(List<Field> fields) {
			this.fields = fields;
		}

		public FieldModel withFields(List<Field> fields) {
			this.fields = fields;
			return this;
		}

		@JsonAnyGetter
		public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		@JsonAnySetter
		public void setAdditionalProperty(String name, Object value) {
			this.additionalProperties.put(name, value);
		}

		public FieldModel withAdditionalProperty(String name, Object value) {
			this.additionalProperties.put(name, value);
			return this;
		}

	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({ "type", "targetbtn", "all_required_fields", "add_fields" })
	public static class Listener {

		@JsonProperty("type")
		private String type;
		@JsonProperty("target_btn")
		private String targetbtn;
		@JsonProperty("all_required_fields")
		private String allRequiredFields;
		@JsonProperty("add_fields")
		private List<String> addFields = null;
		@JsonIgnore
		private Map<String, Object> additionalProperties = new HashMap<String, Object>();

		@JsonProperty("type")
		public String getType() {
			return type;
		}

		@JsonProperty("type")
		public void setType(String type) {
			this.type = type;
		}

		public Listener withType(String type) {
			this.type = type;
			return this;
		}

		@JsonProperty("targetbtn")
		public String getTargetbtn() {
			return targetbtn;
		}

		@JsonProperty("targetbtn")
		public void setTargetbtn(String targetbtn) {
			this.targetbtn = targetbtn;
		}

		public Listener withTargetbtn(String targetbtn) {
			this.targetbtn = targetbtn;
			return this;
		}

		@JsonProperty("all_required_fields")
		public String getAllRequiredFields() {
			return allRequiredFields;
		}

		@JsonProperty("all_required_fields")
		public void setAllRequiredFields(String allRequiredFields) {
			this.allRequiredFields = allRequiredFields;
		}

		public Listener withAllRequiredFields(String allRequiredFields) {
			this.allRequiredFields = allRequiredFields;
			return this;
		}

		@JsonProperty("add_fields")
		public List<String> getAddFields() {
			return addFields;
		}

		@JsonProperty("add_fields")
		public void setAddFields(List<String> addFields) {
			this.addFields = addFields;
		}

		public Listener withAddFields(List<String> addFields) {
			this.addFields = addFields;
			return this;
		}

		@JsonAnyGetter
		public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		@JsonAnySetter
		public void setAdditionalProperty(String name, Object value) {
			this.additionalProperties.put(name, value);
		}

		public Listener withAdditionalProperty(String name, Object value) {
			this.additionalProperties.put(name, value);
			return this;
		}

	}
}
