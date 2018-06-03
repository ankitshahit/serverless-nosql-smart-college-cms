package io.college.cms.core.ui.listener;

import com.vaadin.data.HasValue;
import com.vaadin.ui.AbstractDateField;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSingleSelect;
import com.vaadin.ui.Button;

import io.college.cms.core.ui.util.ElementHelper;
import io.college.cms.core.ui.util.ListenerUtility;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmptyFieldListener<T> implements HasValue.ValueChangeListener<T> {
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

	public EmptyFieldListener() {
		super();
	}

	// we need a source field.
	// we need a list of fields that are requried to be checked.
	// we would require a target field as well.
	// todo: need to include singleselect object in mandatory list as well as
	// other types of elements that need to be considered.
	public EmptyFieldListener(AbstractField<T> field) {
		this.sourceField = field;
	}

	public EmptyFieldListener(AbstractSingleSelect<T> field) {
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
			LOGGER.info(
					"->>> " + sourceField.getConnectorId() + "change event triggered <<<- \n targetBtn is null = {}",
					targetBtn == null);
			ListenerUtility.emptyValueListener(sourceField);
			validation();
		} else if (sourceListField != null
				&& (ListenerUtility.isValidSourceEvent(valueChangeEvent.getComponent(), sourceListField))) {
			LOGGER.info("->>> " + sourceListField.getConnectorId() + "change event triggered \n targetBtn is null = {}",
					targetBtn == null);
			ListenerUtility.emptyValueListener(sourceListField);
			validation();
		} else if (sourceDateField != null
				&& (ListenerUtility.isValidSourceEvent(valueChangeEvent.getComponent(), sourceDateField))) {
			LOGGER.info("->>> " + sourceDateField.getConnectorId() + "change event triggered \n targetBtn is null = {}",
					targetBtn == null);
			ListenerUtility.emptyValueListener(sourceDateField);
			validation();
		}
	}

	private void validation() {
		boolean result = false;

		LOGGER.debug("is targetBtn null = {}", targetBtn == null);
		if (targetBtn != null) {
			if (mandatoryFields != null && mandatoryListFields != null && mandatoryDateFields != null) {
				result = ElementHelper.hasValue(mandatoryFields) && ElementHelper.hasValue(mandatoryListFields)
						&& ElementHelper.hasValue(mandatoryDateFields);
				for (Button btn : targetBtn) {
					btn.setEnabled(result);
				}
			} else if (mandatoryFields != null) {
				result = ElementHelper.hasValue(mandatoryFields);
				for (Button btn : targetBtn) {
					btn.setEnabled(result);
				}
			} else if (mandatoryListFields != null) {
				result = ElementHelper.hasValue(mandatoryListFields);
				for (Button btn : targetBtn) {
					btn.setEnabled(result);
				}
			} else if (mandatoryDateFields != null) {
				result = ElementHelper.hasValue(mandatoryDateFields);
				for (Button btn : targetBtn) {
					btn.setEnabled(result);
				}
			}

			LOGGER.debug("-->> result is: {}", result);
		}
	}
}
