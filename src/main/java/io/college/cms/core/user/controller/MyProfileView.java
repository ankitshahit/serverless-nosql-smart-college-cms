package io.college.cms.core.user.controller;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.data.Binder;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.ui.builder.VaadinWrapper;
import io.college.cms.core.ui.listener.EmptyFieldListener;
import io.college.cms.core.user.model.UserModel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class MyProfileView extends VerticalLayout implements View, Receiver, SucceededListener {

	private static final long serialVersionUID = 1335271694534057987L;
	private Panel rootPanel;
	private VerticalLayout rootLayout;
	private HorizontalSplitPanel horizontalLayout;
	private VerticalLayout firstLayout;
	private HorizontalLayout nameLayout;
	private TextField firstNameFld;
	private TextField mNameFld;
	private TextField lNameFld;
	private DateField birthDateDf;
	private TextField emailFld;
	private PasswordField confirmPasswordFld;
	private ComboBox<String> genderCb;
	private PasswordField passwordFld;

	private VerticalLayout secondLayout;
	private Button viewProfileBtn;
	private Label userStatusLb;
	private Label usernameLbl;
	private TextField phoneTld;
	private Button saveBtn;
	private Upload uploadPic;
	private ProgressBar progressBar;
	@Setter
	private UserModel userModel;
	private Binder<UserModel> binder;

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		if (userModel != null) {
			binder.readBean(userModel);
			usernameLbl.setValue(userModel.getUsername());
			userStatusLb.setValue(userModel.getUserStatus());
		}
	}

	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		View.super.beforeLeave(event);
	}

	@Override
	public void uploadSucceeded(SucceededEvent event) {

	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		try {
			return new FileOutputStream(filename);
		} catch (FileNotFoundException e) {
			LOGGER.error(e.getMessage());
		}
		return null;
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

	@PostConstruct
	public void paint() {
		this.progressBar = new ProgressBar();

		this.uploadPic = new Upload();
		this.uploadPic.setButtonCaption("Upload pic");
		this.rootPanel = new Panel();

		this.rootLayout = new VerticalLayout();

		this.horizontalLayout = new HorizontalSplitPanel();

		this.firstLayout = new VerticalLayout();

		this.nameLayout = new HorizontalLayout();

		this.firstNameFld = new TextField();

		this.mNameFld = new TextField();

		this.lNameFld = new TextField();

		this.birthDateDf = new DateField();

		this.emailFld = new TextField();

		this.passwordFld = new PasswordField();

		this.confirmPasswordFld = new PasswordField();

		this.secondLayout = new VerticalLayout();

		this.viewProfileBtn = new Button();

		this.usernameLbl = new Label();

		this.userStatusLb = new Label();

		this.genderCb = new ComboBox<String>();

		this.phoneTld = new TextField();

		this.saveBtn = new Button();

		this.firstNameFld = VaadinWrapper.builder().caption("First name").placeholder("first name").required(true)
				.visible(true).enabled(true).build().textField();

		this.mNameFld = VaadinWrapper.builder().caption("Middle name").placeholder("Middle name").required(true)
				.visible(true).enabled(true).build().textField();

		this.lNameFld = VaadinWrapper.builder().caption("last name").placeholder("Last name").required(true)
				.visible(true).enabled(true).build().textField();

		this.birthDateDf = VaadinWrapper.builder().caption("Date of birth").required(true).visible(true).enabled(true)
				.denyDateStartRange(true).build().dateField();
		this.birthDateDf.setRangeEnd(LocalDate.now());
		this.emailFld = VaadinWrapper.builder().caption("email address").placeholder("email address").required(true)
				.visible(true).enabled(true).build().textField();

		this.passwordFld = VaadinWrapper.builder().caption("new password").required(true).visible(true).enabled(true)
				.build().passwordField();

		this.confirmPasswordFld = VaadinWrapper.builder().caption("Confirm password").required(true).visible(true)
				.enabled(true).build().passwordField();

		this.viewProfileBtn = VaadinWrapper.builder().caption("View profile pic").required(true).visible(true)
				.enabled(true).build().button();

		this.usernameLbl = VaadinWrapper.builder().caption("Username").required(true).visible(true).enabled(true)
				.build().label();

		this.userStatusLb = VaadinWrapper.builder().caption("User Account status").required(true).visible(true)
				.enabled(true).build().label();

		this.genderCb = (ComboBox<String>) VaadinWrapper.builder().caption("Gender").placeholder("Gender")
				.required(true).visible(true).enabled(true).build().comboBox();
		this.genderCb.setItems("Male", "Female", "Others");

		this.phoneTld = VaadinWrapper.builder().caption("Phone number").placeholder("Phone number").required(true)
				.visible(true).enabled(true).build().textField();

		this.saveBtn = VaadinWrapper.builder().caption("Save settings").required(true).visible(true).enabled(false)
				.build().button();
		this.saveBtn.setStyleName(ValoTheme.BUTTON_PRIMARY);
		this.uploadPic.setReceiver(this);
		this.uploadPic.addSucceededListener(this);
		EmptyFieldListener<LocalDate> birthDateDfListener = new EmptyFieldListener<LocalDate>();
		birthDateDfListener.setSourceDateField(birthDateDf);
		birthDateDfListener.setTargetBtn(saveBtn);
		birthDateDfListener.setMandatoryDateFields(birthDateDf);
		birthDateDfListener.setMandatoryFields(firstNameFld, mNameFld, lNameFld, birthDateDf, emailFld, passwordFld,
				confirmPasswordFld, phoneTld);
		birthDateDfListener.setMandatoryListFields(genderCb);
		birthDateDf.addValueChangeListener(birthDateDfListener);
		EmptyFieldListener<String> passwordFldListener = new EmptyFieldListener<String>();
		passwordFldListener.setSourceField(passwordFld);
		passwordFldListener.setTargetBtn(saveBtn);
		passwordFldListener.setMandatoryFields(firstNameFld, mNameFld, lNameFld, birthDateDf, emailFld, passwordFld,
				confirmPasswordFld, phoneTld);
		passwordFldListener.setMandatoryListFields(genderCb);
		passwordFld.addValueChangeListener(passwordFldListener);
		EmptyFieldListener<String> firstNameFldListener = new EmptyFieldListener<String>();
		firstNameFldListener.setSourceField(firstNameFld);
		firstNameFldListener.setTargetBtn(saveBtn);
		firstNameFldListener.setMandatoryFields(firstNameFld, mNameFld, lNameFld, birthDateDf, emailFld, passwordFld,
				confirmPasswordFld, phoneTld);
		firstNameFldListener.setMandatoryListFields(genderCb);
		firstNameFld.addValueChangeListener(firstNameFldListener);
		EmptyFieldListener<String> phoneTldListener = new EmptyFieldListener<String>();
		phoneTldListener.setSourceField(phoneTld);
		phoneTldListener.setTargetBtn(saveBtn);
		phoneTldListener.setMandatoryFields(firstNameFld, mNameFld, lNameFld, birthDateDf, emailFld, passwordFld,
				confirmPasswordFld, phoneTld);
		phoneTldListener.setMandatoryListFields(genderCb);
		phoneTld.addValueChangeListener(phoneTldListener);
		EmptyFieldListener<String> genderCbListener = new EmptyFieldListener<String>();
		genderCbListener.setSourceListField(genderCb);
		genderCbListener.setTargetBtn(saveBtn);
		genderCbListener.setMandatoryFields(firstNameFld, mNameFld, lNameFld, birthDateDf, emailFld, passwordFld,
				confirmPasswordFld, phoneTld);
		genderCbListener.setMandatoryListFields(genderCb);
		genderCb.addValueChangeListener(genderCbListener);
		EmptyFieldListener<String> confirmPasswordFldListener = new EmptyFieldListener<String>();
		confirmPasswordFldListener.setSourceField(confirmPasswordFld);
		confirmPasswordFldListener.setTargetBtn(saveBtn);
		confirmPasswordFldListener.setMandatoryFields(firstNameFld, mNameFld, lNameFld, birthDateDf, emailFld,
				passwordFld, confirmPasswordFld, phoneTld);
		confirmPasswordFld.addValueChangeListener(confirmPasswordFldListener);
		EmptyFieldListener<String> mNameFldListener = new EmptyFieldListener<String>();
		mNameFldListener.setSourceField(mNameFld);
		mNameFldListener.setTargetBtn(saveBtn);
		mNameFldListener.setMandatoryFields(firstNameFld, mNameFld, lNameFld, birthDateDf, emailFld, passwordFld,
				confirmPasswordFld, phoneTld);
		mNameFldListener.setMandatoryListFields(genderCb);
		mNameFld.addValueChangeListener(mNameFldListener);
		EmptyFieldListener<String> lNameFldListener = new EmptyFieldListener<String>();
		lNameFldListener.setSourceField(lNameFld);
		lNameFldListener.setTargetBtn(saveBtn);
		lNameFldListener.setMandatoryFields(firstNameFld, mNameFld, lNameFld, birthDateDf, emailFld, passwordFld,
				confirmPasswordFld, phoneTld);
		lNameFldListener.setMandatoryListFields(genderCb);
		lNameFld.addValueChangeListener(lNameFldListener);
		EmptyFieldListener<String> emailFldListener = new EmptyFieldListener<String>();
		emailFldListener.setSourceField(emailFld);
		emailFldListener.setTargetBtn(saveBtn);
		emailFldListener.setMandatoryFields(firstNameFld, mNameFld, lNameFld, birthDateDf, emailFld, passwordFld,
				confirmPasswordFld, phoneTld);
		emailFldListener.setMandatoryListFields(genderCb);
		emailFld.addValueChangeListener(emailFldListener);

		firstNameFld.setSizeUndefined();
		mNameFld.setSizeUndefined();
		lNameFld.setSizeUndefined();
		lNameFld.setWidth("2%");
		nameLayout.addComponents(firstNameFld, mNameFld, lNameFld);
		Button changePassword = new Button("Change password?");
		changePassword.setStyleName(ValoTheme.BUTTON_LINK);
		firstLayout.addComponents(nameLayout, genderCb, emailFld, phoneTld, saveBtn);
		firstLayout.setComponentAlignment(saveBtn, Alignment.BOTTOM_RIGHT);
		secondLayout.addComponents(viewProfileBtn, this.uploadPic, usernameLbl, userStatusLb, birthDateDf,
				changePassword);
		horizontalLayout.setSplitPosition(26.5f);
		// rootLayout.addComponents(saveBtn);
		// rootLayout.setComponentAlignment(saveBtn, Alignment.BOTTOM_RIGHT);
		rootPanel.setCaption("My profile");
		rootPanel.setContent(rootLayout);
		Panel panel = new Panel();
		panel.setContent(horizontalLayout);
		panel.setSizeFull();
		rootLayout.addComponent(panel);
		horizontalLayout.addComponents(secondLayout, firstLayout);
		addComponent(rootPanel);
		binder = new Binder<>();
		binder.bind(this.firstNameFld, UserModel::getFirstName, UserModel::setFirstName);
		binder.bind(this.mNameFld, UserModel::getMiddleName, UserModel::setMiddleName);
		binder.bind(this.lNameFld, UserModel::getLastName, UserModel::setLastName);
		binder.bind(this.genderCb, UserModel::getGender, UserModel::setGender);
		binder.bind(this.birthDateDf, UserModel::getDateOfBirth, UserModel::setDateOfBirth);
		binder.bind(this.phoneTld, UserModel::getPhone, UserModel::setPhone);
		this.saveBtn.addClickListener(click -> {
			try {
				UserModel model = UserModel.builder().build();
				this.binder.writeBean(model);
			} catch (Exception ex) {
				LOGGER.error(ex.getMessage());
			}

		});
	}

}
