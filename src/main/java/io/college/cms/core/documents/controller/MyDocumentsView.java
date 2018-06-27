package io.college.cms.core.documents.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.Utils;
import io.college.cms.core.ui.builder.MessagePopupView;
import io.college.cms.core.ui.builder.VaadinWrapper;
import io.college.cms.core.ui.services.CoreUiService;
import io.college.cms.core.ui.util.ListenerUtility;
import io.college.cms.core.upload.services.UploadResponseService;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class MyDocumentsView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;
	private CoreUiService coreUI;
	private MyDocumentsViewService myDocumentsService;
	private UploadResponseService uploadResponseService;

	/**
	 * @param coreUI
	 */
	public MyDocumentsView(CoreUiService coreUI, UploadResponseService uploadResponseService) {
		super();
		this.coreUI = coreUI;
		this.uploadResponseService = uploadResponseService;
		this.myDocumentsService = new MyDocumentsViewService(this.coreUI, this.uploadResponseService);
	}

	@PostConstruct
	protected void paint() {
		addListeners();
		addComponent(this.myDocumentsService.rootPanel);
	}

	protected void addListeners() {
		this.myDocumentsService.tagHelpBtn.addClickListener(click -> {
			if (!ListenerUtility.isValidSourceEvent(click.getComponent(), this.myDocumentsService.tagHelpBtn)) {
				return;
			}
			MessagePopupView messagePopupView = new MessagePopupView("What's this",
					"<b>Tag a document</b>: Provide unique tags like 'HSC marksheet', it helps staff to review documents easily. <br/>It maybe requested to add a #tag mentioned exactly.");
			messagePopupView.center();

			getUI().addWindow(messagePopupView);
		});
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
		Button tagHelpBtn;
		Label tagFieldLbl;
		UploadResponseService uploadResponseService;
		String username;
		TextField filenameFld;
		Label usernameLbl;
		Label s3Link;

		/**
		 * @param coreUI
		 */
		public MyDocumentsViewService(CoreUiService coreUI, UploadResponseService uploadResponseService) {
			super();
			this.uploadResponseService = uploadResponseService;
			this.coreUI = coreUI;
			this.heading = coreUI.getLabel();
			this.upload = new Upload();
			this.tagField = new TextField();
			this.documentsList = new ListSelect<>();
			this.saveBtn = new Button();
			this.tagHelpBtn = new Button();
			this.rootPanel = new Panel();
			this.helpMyDocumentBtn = new Button();
			this.download = new Button();
			this.downloadAll = new Button();
			this.tagFieldLbl = coreUI.getLabel();
			this.usernameLbl = coreUI.getLabel();
			this.filenameFld = VaadinWrapper.builder().build().textField();
			this.s3Link = coreUI.getLabel();
			initUI();
		}

		void initUI() {
			initAttributes();
			initStyle();
			initListener();
			initComponents();
		}

		void initAttributes() {
			this.heading.setValue("<p><b>My documents</p>");
			this.helpMyDocumentBtn.setIcon(VaadinIcons.QUESTION);
			this.upload.setImmediateMode(true);
			this.upload.setButtonCaption("Select file");
			this.tagFieldLbl.setValue("");
			this.tagField.setCaption("<p><b>Tag document</b></p>");
			this.tagField.setCaptionAsHtml(true);
			this.tagField.setPlaceholder("Provide an unique tag name for document");
			this.tagField.setRequiredIndicatorVisible(true);
			this.tagHelpBtn.setIcon(VaadinIcons.QUESTION);
			this.download.setCaption("View file");
			this.download.setEnabled(false);
			this.downloadAll.setCaption("Download all files!");
			this.filenameFld.setCaptionAsHtml(true);
			this.filenameFld.setCaption("<p><b>File name</b></p>");
			this.filenameFld.setPlaceholder("Provide file name");
		}

		void initStyle() {
			this.helpMyDocumentBtn.addStyleNames(ValoTheme.BUTTON_ICON_ONLY);
			// this.upload.setButtonStyleName(ValoTheme.BUTTON_ICON_ALIGN_TOP);
			this.download.addStyleNames(ValoTheme.BUTTON_FRIENDLY);
			this.downloadAll.addStyleNames(ValoTheme.BUTTON_FRIENDLY);
			this.tagField.addStyleNames(ValoTheme.TEXTFIELD_ALIGN_CENTER, ValoTheme.TEXTFIELD_INLINE_ICON,
					ValoTheme.TEXTFIELD_LARGE);
			this.filenameFld.addStyleNames(ValoTheme.TEXTFIELD_ALIGN_CENTER, ValoTheme.TEXTFIELD_INLINE_ICON,
					ValoTheme.TEXTFIELD_LARGE);
			this.filenameFld.setSizeFull();
			this.tagField.setIcon(VaadinIcons.ADD_DOCK);
			// this.tagField.setIcon(VaadinIcons.ADD_DOCK);
			// this.tagField.setSizeFull();
			this.tagHelpBtn.addStyleNames(ValoTheme.BUTTON_ICON_ONLY, ValoTheme.BUTTON_QUIET);
			this.documentsList.setSizeFull();
		}

		void initComponents() {
			VerticalLayout rootLayout = new VerticalLayout();
			VerticalLayout firstLayout = new VerticalLayout();
			VerticalLayout secondLayout = new VerticalLayout();
			Label uploadDocumentLbl = coreUI.getLabel();
			uploadDocumentLbl.setValue("<b>Upload documents </b>&nbsp;&nbsp;");
			HorizontalLayout hl = new HorizontalLayout();
			hl.setSizeFull();
			this.tagField.setSizeFull();

			hl.addComponents(this.tagField);
			this.usernameLbl.setCaption("<b>User</b>:");
			this.usernameLbl.setValue("File is being uploaded with username: <b>'Ankit'</b>");
			Label tagHelpLbl = new Label();

			tagHelpLbl.setValue(
					"<b>Tag</b>: helps define and find file easier, for example tagging a document as <i>HSC</i> <br/>will help other's to look directly and assume it's related to HSC marksheet.");
			tagHelpLbl.setContentMode(ContentMode.HTML);
			firstLayout.addComponents(hl, tagHelpLbl, this.usernameLbl);
			Label helpLbl = new Label();
			helpLbl.setSizeFull();
			helpLbl.setValue("<b>Help</b>");
			helpLbl.setContentMode(ContentMode.HTML);
			secondLayout.addComponents(new HorizontalLayout(helpLbl, this.tagHelpBtn), uploadDocumentLbl, this.upload,
					this.s3Link, this.download);
			HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
			splitPanel.setSplitPosition(70.0f);
			splitPanel.addComponents(firstLayout, secondLayout);
			rootLayout.addComponents(splitPanel);
			this.rootPanel.setContent(rootLayout);

		}

		void initListener() {
			this.documentsList.addValueChangeListener(value -> {
				if (!ListenerUtility.isValidSourceEvent(value.getComponent(), this.documentsList)) {
					return;
				}
				this.download.setEnabled(this.documentsList.getOptionalValue().isPresent());
			});
			ImageUploader imageReceiver = new ImageUploader(this.uploadResponseService);
			imageReceiver.setTag(this.tagField);
			imageReceiver.setUsername(username);
			imageReceiver.setS3Link(this.s3Link);
			imageReceiver.setViewImage(download);
			this.upload.setReceiver(imageReceiver);
			this.upload.addSucceededListener(imageReceiver);
		}
	}

	// Implement both receiver that saves upload in a file and
	// listener for successful upload
	public static class ImageUploader implements Receiver, SucceededListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private File file;
		private UploadResponseService uploadService;
		@Setter
		private String username;
		@Setter
		private TextField tag;
		@Setter
		private Label s3Link;
		@Setter
		private Button viewImage;
		final ByteArrayOutputStream bas = new ByteArrayOutputStream();

		/**
		 * @param uploadService
		 */
		public ImageUploader(UploadResponseService uploadService) {
			super();
			this.uploadService = uploadService;
		}

		public OutputStream receiveUpload(String filename, String mimeType) {
			try {
				return new FileOutputStream(filename);
			} catch (FileNotFoundException e) {
				LOGGER.error(e.getMessage());
			}
			return null;
		}

		public void uploadSucceeded(SucceededEvent event) {

			this.file = new File(event.getFilename());
			FactoryResponse fr = this.uploadService.uploadFile(this.file, this.username, this.tag.getValue());
			Utils.showFactoryResponseMsg(fr, "Unable to upload file", "Successfully uploaded file");
			if (Utils.isSuccess(fr)) {
				this.s3Link.setValue(
						new StringBuilder().append("<p><b>Download file</b>:</p> <a href=").append(fr.getResponse())
								.append(" target=_blank>").append(event.getFilename()).append("</a>").toString());

				try {
					viewImage.addClickListener(click -> {
						if (event.getMIMEType().startsWith("image")) {
							Image image = new Image();
							image.setSource(new FileResource(this.file));
							showComponent(image, this.file.getName());
						} else {
							Label label = new Label();
							label.setContentMode(ContentMode.HTML);
							label.setValue(file.getName());
							showComponent(label, this.file.getName());
						}
					});
					viewImage.setEnabled(true);

				} catch (Exception ex) {
					LOGGER.error(ex.getMessage());
				}

			}
		}

		private void showComponent(Component embedded, String name) {
			final VerticalLayout layout = new VerticalLayout();

			layout.setSizeUndefined();
			layout.setMargin(true);
			final Window w = new Window(name, layout);
			w.addStyleName("dropdisplaywindow");
			w.setSizeUndefined();
			w.setResizable(false);
			w.center();
			layout.addComponent(embedded);
			UI.getCurrent().addWindow(w);
		}
	}

}
