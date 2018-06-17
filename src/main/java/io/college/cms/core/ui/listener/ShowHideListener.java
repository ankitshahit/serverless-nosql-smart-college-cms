package io.college.cms.core.ui.listener;

import com.vaadin.data.HasValue;
import com.vaadin.ui.AbstractDateField;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSingleSelect;
import com.vaadin.ui.Button;

import io.college.cms.core.ui.util.ElementHelper;
import io.college.cms.core.ui.util.ListenerUtility;
import lombok.var;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShowHideListener<T> implements HasValue.ValueChangeListener<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AbstractField<T> sourceField;
	private AbstractSingleSelect<T> sourceListField;
	private AbstractDateField sourceDateField;
	private Button[] targetBtn;
	private AbstractField<?>[] mandatoryFields;
	private AbstractSingleSelect<?>[] mandatoryListFields;
	private AbstractDateField[] mandatoryDateFields;

	public ShowHideListener() {
		super();
	}

	// we need a source field.
	// we need a list of fields that are requried to be checked.
	// we would require a target field as well.
	// todo: need to include singleselect object in mandatory list as well as
	// other types of elements that need to be considered.
	public ShowHideListener(AbstractField<T> field) {
		this.sourceField = field;
	}

	public ShowHideListener(AbstractSingleSelect<T> field) {
		this.sourceListField = field;
	}

	public void setSourceDateField(AbstractDateField sourceDateField) {
		this.sourceDateField = sourceDateField;
	}

	public void setTargetBtn(Button... targetBtn) {
		this.targetBtn = targetBtn;
	}

	public void setMandatoryFields(AbstractField<?>... mandatoryFields) {
		this.mandatoryFields = mandatoryFields;
	}

	public void setSourceField(AbstractField<T> field) {
		this.sourceField = field;
	}

	public void setMandatoryListFields(AbstractSingleSelect<?>... abstractSingleSelects) {
		this.mandatoryListFields = abstractSingleSelects;
	}

	public void setSourceListField(AbstractSingleSelect<T> sourceListField) {
		this.sourceListField = sourceListField;
	}

	public void setMandatoryDateFields(AbstractDateField... mandatoryDateFields) {
		this.mandatoryDateFields = mandatoryDateFields;
	}

	@Override
	public void valueChange(@SuppressWarnings("rawtypes") HasValue.ValueChangeEvent valueChangeEvent) {

		if (sourceField == null && sourceListField == null && sourceDateField == null) {
			return;
		}
		if (sourceField != null && (ListenerUtility.isValidSourceEvent(valueChangeEvent.getComponent(), sourceField))) {
			validation();
		} else if (sourceListField != null
				&& (ListenerUtility.isValidSourceEvent(valueChangeEvent.getComponent(), sourceListField))) {

			validation();
		} else if (sourceDateField != null
				&& (ListenerUtility.isValidSourceEvent(valueChangeEvent.getComponent(), sourceDateField))) {

			validation();
		}
	}

	private void validation() {

		boolean result = !(ElementHelper.hasValue(sourceField) || ElementHelper.hasValue(sourceListField)
				|| ElementHelper.hasValue(sourceDateField));
		if (mandatoryFields != null && mandatoryFields.length > 0) {
			for (AbstractField<?> field : mandatoryFields) {
				field.setVisible(result);
			}
		}
		if (mandatoryListFields != null && mandatoryListFields.length > 0) {
			for (AbstractSingleSelect<?> field : mandatoryListFields) {
				field.setVisible(result);
			}
		}
		if (mandatoryDateFields != null && mandatoryDateFields.length > 0) {
			for (AbstractDateField field : mandatoryDateFields) {
				field.setVisible(result);
			}
		}
		LOGGER.debug("-->> result is: {}", result);
	}
}
