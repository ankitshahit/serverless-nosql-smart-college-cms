package io.college.cms.core.documents.controller;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.ui.services.CoreUiService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class MyDocumentsView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;
	private CoreUiService coreUI;
	private MyDocumentsViewService myDocumentsService;

	/**
	 * @param coreUI
	 */
	public MyDocumentsView(CoreUiService coreUI) {
		super();
		this.coreUI = coreUI;
		this.myDocumentsService = new MyDocumentsViewService(this.coreUI);
	}

	@PostConstruct
	protected void paint() {
		addComponent(this.myDocumentsService.rootPanel);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		try {
			LOGGER.debug("request received view : {}", event);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			Notification notifi = Notification.show("", Type.ERROR_MESSAGE);
			notifi.setCaption("Application error");
			notifi.setIcon(VaadinIcons.STOP_COG);
			notifi.setDescription(
					"We were unable to process request for some reason! Please try again later or contact admin");
			notifi.setDelayMsec(Notification.DELAY_FOREVER);
		}
	}

	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		View.super.beforeLeave(event);
	}

	@Data
	private static class MyDocumentsViewService {
		Label heading;
		Upload upload;
		TextField tagField;
		ListSelect<String> documentsList;
		CoreUiService coreUI;
		Button saveBtn;
		Panel rootPanel;
		Button helpMyDocumentBtn;
		Button download;
		Button downloadAll;

		/**
		 * @param coreUI
		 */
		public MyDocumentsViewService(CoreUiService coreUI) {
			super();
			this.coreUI = coreUI;
			this.heading = coreUI.getLabel();
			this.upload = new Upload();
			this.tagField = new TextField();
			this.documentsList = new ListSelect<>();
			this.saveBtn = new Button();
			this.rootPanel = new Panel();
			this.helpMyDocumentBtn = new Button();
			this.download = new Button();
			this.downloadAll = new Button();
			initUI();
		}

		void initUI() {
			initAttributes();
			initStyle();
			initListener();
			initComponents();
		}

		void initAttributes() {
			this.heading.setValue("<p><b>Tag a document</b>: click on ? button to read more!</p>");
			this.helpMyDocumentBtn.setIcon(VaadinIcons.QUESTION);
			this.upload.setButtonCaption("Upload!");
			this.tagField.setPlaceholder("Provide an unique tag name for document");
			this.tagField.setCaption("Tag document");
			this.tagField.setRequiredIndicatorVisible(true);
			this.download.setCaption("Download");
			this.downloadAll.setCaption("Download all files!");
		}

		void initStyle() {
			this.helpMyDocumentBtn.addStyleNames(ValoTheme.BUTTON_ICON_ONLY);
			this.upload.setButtonStyleName(ValoTheme.BUTTON_ICON_ALIGN_TOP);
			this.download.addStyleNames(ValoTheme.BUTTON_FRIENDLY);
			this.downloadAll.addStyleNames(ValoTheme.BUTTON_FRIENDLY);
		}

		void initListener() {

		}

		void initComponents() {
			VerticalLayout rootLayout = new VerticalLayout();
			VerticalLayout firstLayout = new VerticalLayout();
			VerticalLayout secondLayout = new VerticalLayout();

			firstLayout.addComponents(this.heading, this.upload, this.tagField);
			secondLayout.addComponents(this.documentsList, this.download);
			HorizontalLayout buttonLayout = new HorizontalLayout();
			buttonLayout.addComponents(this.downloadAll, this.saveBtn);
			HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
			splitPanel.addComponents(firstLayout, secondLayout);
			rootLayout.addComponents(splitPanel, buttonLayout);
			rootLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_RIGHT);
			this.rootPanel.setContent(rootLayout);
		}
	}

}
