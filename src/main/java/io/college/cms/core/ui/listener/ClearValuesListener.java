package io.college.cms.core.ui.listener;

import com.vaadin.ui.AbstractDateField;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractMultiSelect;
import com.vaadin.ui.AbstractSingleSelect;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClearValuesListener<T> implements ClickListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private AbstractField<?>[] mandatoryFields;
	private AbstractSingleSelect<?>[] mandatoryListFields;
	private AbstractDateField[] mandatoryDateFields;
	private AbstractMultiSelect<?>[] multiSelect;

	public ClearValuesListener() {
		super();
	}

	public void setMandatoryFields(AbstractField<?>... mandatoryFields) {
		this.mandatoryFields = mandatoryFields;
	}

	public void setMandatoryListFields(AbstractSingleSelect<?>... abstractSingleSelects) {
		this.mandatoryListFields = abstractSingleSelects;
	}

	public void setMandatoryListFields(AbstractMultiSelect<?>... abstractMultiSelect) {
		this.multiSelect = abstractMultiSelect;
	}

	public void setMandatoryDateFields(AbstractDateField... mandatoryDateFields) {
		this.mandatoryDateFields = mandatoryDateFields;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		LOGGER.info("Clear values listener");
		validation();

	}

	private void validation() {

		LOGGER.debug("is targetBtn null = {}");
		if (mandatoryDateFields != null && mandatoryDateFields.length > 0) {
			for (AbstractDateField date : mandatoryDateFields) {
				date.setComponentError(null);
				date.clear();
			}
		}
		if (mandatoryListFields != null && mandatoryListFields.length > 0) {
			for (AbstractSingleSelect<?> element : mandatoryListFields) {
				element.setComponentError(null);
				element.clear();
			}
		}
		if (mandatoryFields != null && mandatoryFields.length > 0) {
			for (AbstractField<?> element : mandatoryFields) {
				element.setComponentError(null);
				element.clear();
			}
		}
		if (this.multiSelect != null && this.multiSelect.length > 0) {
			for (AbstractMultiSelect<?> element : this.multiSelect) {
				element.setComponentError(null);
				element.clear();
			}
		}

	}

}
