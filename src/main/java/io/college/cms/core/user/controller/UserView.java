package io.college.cms.core.user.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Composite;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.application.Utils;
import io.college.cms.core.ui.builder.DeletePopupView;
import io.college.cms.core.ui.builder.TextFieldWrapper;
import io.college.cms.core.ui.listener.EmptyFieldListener;
import io.college.cms.core.ui.util.ListenerUtility;
import io.college.cms.core.user.constants.UserAttributes;
import io.college.cms.core.user.model.UserModel;
import io.college.cms.core.user.service.UserResponseService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class UserView extends Composite implements View {

	private static final long serialVersionUID = 1L;
	private UserViewService userViewService;
	private UserResponseService userResponseService;
	private UserModel userModel;

	public UserView() {
		super();
		this.userViewService = new UserViewService();
	}

	@Autowired
	public void setUserResponseService(UserResponseService userResponseService) {
		this.userResponseService = userResponseService;
	}

	@PostConstruct()
	protected void paint() {
		// adding click listener, that we need to execute stuff on click of
		// a
		// button in ui
		this.userViewService.getArchiveUser().addClickListener(click -> {
			DeletePopupView deleteView = new DeletePopupView();
			deleteView.show(getUI(), clickData -> {
				deleteView.setVisible(false);
				FactoryResponse fr = FactoryResponse.builder().build();
				if (fr == null || SummaryMessageEnum.SUCCESS != fr.getSummaryMessage()) {
					deleteView.getUnsuccessfullNotification(String.valueOf(fr.getResponse()));
				} else {
					deleteView.getDeleteNotification();
				}
				clickData.getComponent().setVisible(false);
				clickData.getComponent().getParent().setVisible(false);
			});
		});
		EmptyFieldListener<String> usernameTextFieldList = new EmptyFieldListener<>();
		usernameTextFieldList.setSourceField(this.userViewService.getUsername());
		usernameTextFieldList.setMandatoryFields(this.userViewService.getUsername());
		usernameTextFieldList.setTargetBtn(this.userViewService.getValidateUsername());
		this.userViewService.getUsername().addValueChangeListener(usernameTextFieldList);

		this.userViewService.validateUsername.addClickListener(click -> {
			FactoryResponse fr = FactoryResponse.builder().build();
			if (!ListenerUtility.isValidSourceEvent(click.getComponent(), this.userViewService.validateUsername)) {
				return;
			}
			// if the userModel is null, means event is triggered by user
			// and not programmatically from select users screen.
			// we wanna check for field values to validate against. like
			// username
			if (userModel == null) {
				fr = userResponseService.getUserByUser(null,
						Utils.val(this.userViewService.getUsername().getOptionalValue()));
				// if the request is success means we have an existing
				// username available in database
				// TODO: have to compare msg for failure, in-case it says
				// resource is not found only the would we allow the user to
				// continue further.
				if (fr != null && SummaryMessageEnum.SUCCESS == fr.getSummaryMessage()) {
					this.userViewService.getUsername().setCaption("Username is already taken.");
					this.userViewService.getUsername().setCaptionAsHtml(true);
					Notification.show("Username is not available, please try new username", Type.ERROR_MESSAGE)
							.setDelayMsec(Notification.DELAY_FOREVER);

				} else {
					Notification notifi = Notification.show("Username is available, click on message to continue!",
							Type.HUMANIZED_MESSAGE);
					notifi.setDelayMsec(Notification.DELAY_FOREVER);
					notifi.addCloseListener(close -> {
						this.userViewService.getPasswordField().setVisible(true);
						this.userViewService.getConfirmPasswordField().setVisible(true);
						this.userViewService.getEmailAddress().setVisible(true);
						this.userViewService.getValidateUsername().setVisible(false);
						this.userViewService.getSaveNext().setVisible(true);
						this.userViewService.getUsername().setEnabled(false);
						this.userViewService.getUsername().setCaption("<b style=color:blue>Username selected</b>:");
						this.userViewService.getUsername().addStyleNames(ValoTheme.TEXTFIELD_ALIGN_RIGHT,
								ValoTheme.TEXTFIELD_SMALL);

						this.userViewService.getUsername().setCaptionAsHtml(true);
						this.userViewService.getPasswordPolicy()
								.setValue("Password should be greater than or equal to 8 characters");
					});

				}

			}
		});

		setCompositionRoot(this.userViewService.getRootPanel());
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);

		try {
			this.userViewService.getArchiveUser().setVisible(userModel != null);
			if (userModel == null) {
				this.userViewService.getPasswordField().setVisible(false);
				this.userViewService.getConfirmPasswordField().setVisible(false);
				this.userViewService.getEmailAddress().setVisible(false);
				this.userViewService.getValidateUsername().setVisible(true);
				this.userViewService.getSaveNext().setVisible(false);
				this.userViewService.getPasswordPolicy().setValue(
						"Please enter an username, when done click on confirm.\n System will let you know of availability");
				this.userViewService.getValidateUsername().setEnabled(false);
				this.userViewService.getAccordin().getTab(1).setEnabled(false);

			}
			this.userViewService.getSaveNext().addClickListener(click -> {
				FactoryResponse fr = FactoryResponse.builder().build();
				if (!ListenerUtility.isValidSourceEvent(click.getComponent(), this.userViewService.getSaveNext())) {
					return;
				}

				UserModel user = UserModel.builder()
						.username(Utils.val(this.userViewService.getUsername().getOptionalValue()))
						.token(Utils.val(this.userViewService.getPasswordField().getOptionalValue()))
						.withAttribute(UserModel.AttributeType.builder().name(UserAttributes.EMAIL)
								.value(Utils.val(this.userViewService.getEmailAddress().getOptionalValue())).build())
						.build();

				/*
				 * UserModel userModel = UserModel.builder()
				 * .withAttribute(UserModel.AttributeType.builder().name(
				 * UserAttributes.GIVEN_NAME)
				 * .value(Utils.val(this.userViewService.getFirstName().
				 * getOptionalValue())).build())
				 * .withAttribute(UserModel.AttributeType.builder().name(
				 * UserAttributes.MIDDLE_NAME)
				 * .value(Utils.val(this.userViewService.getMiddleName().
				 * getOptionalValue())).build())
				 * .withAttribute(UserModel.AttributeType.builder().name(
				 * UserAttributes.FAMILY_NAME)
				 * .value(Utils.val(this.userViewService.getLastName().
				 * getOptionalValue())).build())
				 * .email(Utils.val(this.userViewService.getEmailAddress().
				 * getOptionalValue()))
				 * .token(Utils.val(this.userViewService.getPasswordField().
				 * getOptionalValue()))
				 * .createdOn(LocalDate.now()).dateOfBirth(this.userViewService.
				 * getDateOfBirth().getValue())
				 * .gender(Utils.val(this.userViewService.getGender().
				 * getOptionalValue())).build();
				 */

				fr = userResponseService.createUpdateUser(null, user);
				Notification notifi = Utils.showFactoryResponseMsg(fr);
				if (fr != null && SummaryMessageEnum.SUCCESS == fr.getSummaryMessage()) {
					notifi.addCloseListener(close -> {
						this.userViewService.getAccordin().getTab(1).setEnabled(true);
						this.userViewService.getAccordin().setSelectedTab(1);
					});
				}

			});
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
	private static class UserViewService {

		/**
		 * Vaadin UI elements instance
		 */
		private VerticalLayout rootLayout;
		private Panel rootPanel;
		private TextField firstName;
		private TextField middleName;
		private TextField lastName;
		private TextField emailAddress;
		private DateField dateOfBirth;
		private ComboBox<String> gender;
		private PasswordField passwordField;
		private PasswordField confirmPasswordField;
		private Label passwordPolicy;
		private Button archiveUser;
		private Button saveNext;
		private Accordion accordin;
		private TextField username;
		private Button validateUsername;
		private GridLayout gridLayout2;
		private Button saveAttributes;
		private Upload upload;
		public Image profile;

		private UserViewService() {
			initUI();
			paint();
			style();
		}

		/**
		 * UI elements or instances need a place to be initialized, as well to
		 * avoid duplication in-case there are multiple constructor to
		 * initialize consolidating instantiating to a method will help to
		 * simply call from the constructor or postconstruct whichever is more
		 * suitable.
		 */
		@SuppressWarnings("deprecation")
		protected void initUI() {
			int tabIndex = 0;

			this.username = TextFieldWrapper.builder().required(false).placeholder("Username")
					.icon(VaadinIcons.NEWSPAPER).description("Select a unique username, to identify throughout.	")
					.build().textField();
			this.username.addStyleNames(ValoTheme.TEXTFIELD_HUGE, ValoTheme.TEXTFIELD_INLINE_ICON,
					ValoTheme.TEXTFIELD_ALIGN_CENTER, ValoTheme.TEXTFIELD_BORDERLESS);
			this.username.setSizeFull();
			this.username.setTabIndex(++tabIndex);
			// setting attributes for first name
			this.firstName = TextFieldWrapper.builder().placeholder("First name").enabled(true).required(false)
					.icon(VaadinIcons.USER_STAR).build().textField();
			this.firstName.setTabIndex(++tabIndex);
			// setting attributes for second name
			this.middleName = TextFieldWrapper.builder().placeholder("Middle name").enabled(true).required(false)
					.icon(VaadinIcons.USER_STAR).build().textField();
			this.middleName.setTabIndex(++tabIndex);
			// setting attributes for last name
			this.lastName = TextFieldWrapper.builder().placeholder("Last name").enabled(true).required(false)
					.icon(VaadinIcons.USER_STAR).build().textField();
			this.lastName.setTabIndex(++tabIndex);

			// setting attributes for date field.
			this.dateOfBirth = new DateField();
			// this.dateOfBirth.setRequiredIndicatorVisible(true);
			this.dateOfBirth.setPlaceholder("Date of birth");
			// TODO: need to import a better set of icons into spring
			// this.dateOfBirth.setIcon(VaadinIcons.DATE_INPUT);
			this.dateOfBirth.setDateOutOfRangeMessage("Date is out of range");
			this.dateOfBirth.setResponsive(true);
			this.dateOfBirth.setRangeEnd(LocalDate.now());
			this.dateOfBirth.setTabIndex(++tabIndex);
			this.dateOfBirth.addStyleNames(ValoTheme.DATEFIELD_BORDERLESS, ValoTheme.DATEFIELD_LARGE);
			// setting attributes for gender
			this.gender = new ComboBox<String>();
			this.gender.setPlaceholder("Gender");
			// this.gender.setRequiredIndicatorVisible(true);
			this.gender.setTabIndex(++tabIndex);

			// setting attributes for email address.
			this.emailAddress = TextFieldWrapper.builder().enabled(true).required(false).icon(VaadinIcons.USER_STAR)
					.placeholder("Email Address").build().textField();
			this.emailAddress.setTabIndex(++tabIndex);

			// setting attributes for passwordfield
			this.passwordField = new PasswordField();
			this.passwordField.setPlaceholder("New password");
			this.passwordField.setIcon(VaadinIcons.PASSWORD);
			this.passwordField.setRequiredIndicatorVisible(false);
			this.passwordField.setTabIndex(++tabIndex);
			this.passwordField.setSizeFull();
			// setting attributes for confirm password field policy
			this.confirmPasswordField = new PasswordField();
			this.confirmPasswordField.setPlaceholder("Confirm password");
			this.confirmPasswordField.setIcon(VaadinIcons.PASSWORD);
			this.confirmPasswordField.setRequiredIndicatorVisible(false);
			this.confirmPasswordField.setTabIndex(++tabIndex);

			// no attributes are required at the moment, as there is no password
			// policy
			this.passwordPolicy = new Label("Password should be greater than or equal to 8 characters");

			// setting attributes for archive user button
			this.archiveUser = new Button("<b style=color:red>Archive</b>");

			this.archiveUser.setHtmlContentAllowed(true);
			this.archiveUser.addStyleNames(ValoTheme.BUTTON_BORDERLESS, ValoTheme.BUTTON_DANGER);
			// setting attributes for save next button.
			this.saveNext = new Button("<b style=color:white>Save & Next</b>");
			this.saveNext.setHtmlContentAllowed(true);
			this.saveNext.setStyleName(ValoTheme.BUTTON_PRIMARY);
			this.saveNext.setTabIndex(++tabIndex);
			this.saveNext.setEnabled(false);
			this.validateUsername = new Button("Check Availability!");
			this.validateUsername.addStyleNames(ValoTheme.BUTTON_PRIMARY);
			this.validateUsername.setVisible(false);
			this.gridLayout2 = new GridLayout();
			this.saveAttributes = new Button("Save Attributes");
			this.saveAttributes.addStyleNames(ValoTheme.BUTTON_PRIMARY);
			this.profile = new Image("Profile picture");

			ImageUploader receiver = new ImageUploader(this.profile);
			this.upload = new Upload("Upload", receiver);
			this.upload.setImmediateMode(false);
			this.upload.setButtonCaption("Upload profile picture");
			this.upload.addSucceededListener(receiver);

			this.accordin = new Accordion();

		}

		@SuppressWarnings("unchecked")
		protected void paint() {
			this.rootPanel = new Panel();
			this.rootLayout = new VerticalLayout();
			style();
			// grouping into as one layout to handle positioning of fields and
			// styling better: combining all fields related to name
			HorizontalLayout nameCssLayout = new HorizontalLayout();
			nameCssLayout.addComponents(this.firstName, this.middleName, this.lastName);

			// combining all fields related to button on step one screen
			HorizontalLayout buttonCssLayout = new HorizontalLayout();
			buttonCssLayout.addComponents(this.archiveUser, this.saveNext);

			// setting items because we need these values to appear in a
			// combobox.
			this.gender.setItems("Male", "Female", "Others");

			HorizontalLayout dbAndGender = new HorizontalLayout();
			dbAndGender.addComponents(this.dateOfBirth, this.gender);
			dbAndGender.setComponentAlignment(this.gender, Alignment.TOP_RIGHT);
			dbAndGender.setSizeFull();
			this.saveNext.addClickListener(click -> {
				if (!ListenerUtility.isValidSourceEvent(click.getComponent(), this.saveNext)) {
					return;
				}
			});

			EmptyFieldListener<String> usernameListener = (EmptyFieldListener<String>) getValueChangeListener();
			usernameListener.setSourceField(this.username);
			this.username.addValueChangeListener(usernameListener);

			EmptyFieldListener<String> firstNameListener = (EmptyFieldListener<String>) getValueChangeListener();
			firstNameListener.setSourceField(this.firstName);
			this.firstName.addValueChangeListener(firstNameListener);

			EmptyFieldListener<String> middleNameListener = (EmptyFieldListener<String>) getValueChangeListener();
			middleNameListener.setSourceField(this.middleName);
			this.middleName.addValueChangeListener(middleNameListener);

			EmptyFieldListener<String> lastNameListener = (EmptyFieldListener<String>) getValueChangeListener();
			lastNameListener.setSourceField(this.lastName);
			this.lastName.addValueChangeListener(lastNameListener);

			EmptyFieldListener<String> emailListener = (EmptyFieldListener<String>) getValueChangeListener();
			emailListener.setSourceField(this.emailAddress);
			this.emailAddress.addValueChangeListener(emailListener);

			EmptyFieldListener<String> passwordListener = (EmptyFieldListener<String>) getValueChangeListener();
			passwordListener.setSourceField(this.passwordField);
			this.passwordField.addValueChangeListener(passwordListener);

			EmptyFieldListener<String> passwordPolicyListener = (EmptyFieldListener<String>) getValueChangeListener();
			passwordPolicyListener.setSourceField(this.confirmPasswordField);
			this.confirmPasswordField.addValueChangeListener(passwordPolicyListener);

			EmptyFieldListener<LocalDate> dateBirthListener = (EmptyFieldListener<LocalDate>) getValueChangeListener();
			dateBirthListener.setSourceDateField(this.dateOfBirth);
			this.dateOfBirth.addValueChangeListener(dateBirthListener);

			EmptyFieldListener<String> comboBoxListener = (EmptyFieldListener<String>) getValueChangeListener();
			comboBoxListener.setSourceListField(this.gender);
			this.gender.addValueChangeListener(comboBoxListener);

			GridLayout grid = new GridLayout();
			// nameCssLayout, dbAndGender,
			grid.addComponents(new CssLayout(this.username), new CssLayout(this.emailAddress), this.passwordPolicy,
					new CssLayout(this.passwordField), new CssLayout(this.confirmPasswordField), this.validateUsername,
					buttonCssLayout);

			grid.setSpacing(true);
			grid.setComponentAlignment(buttonCssLayout, Alignment.MIDDLE_RIGHT);

			// second tab in accordin elements.
			HorizontalLayout profilePictureLayout = new HorizontalLayout(this.profile, this.upload);
			profilePictureLayout.setSizeFull();
			this.upload.addSucceededListener(succeed -> {
				Notification.show("Uploaded.");
			});
			dbAndGender.setSizeFull();
			dbAndGender.setComponentAlignment(this.gender, Alignment.MIDDLE_RIGHT);
			gridLayout2.addComponents(profilePictureLayout, nameCssLayout, dbAndGender, this.saveAttributes);
			gridLayout2.setComponentAlignment(this.saveAttributes, Alignment.BOTTOM_RIGHT);
			gridLayout2.setSpacing(true);
			gridLayout2.setSizeFull();
			this.accordin.addTab(grid, "Step 1/2");
			this.accordin.addTab(gridLayout2, "Step 2/2");

			// we need to add accordin to a panel and panel to a vertical layout
			// to give a proper look and feel
			this.rootPanel.setContent(this.accordin);
			this.rootPanel.setSizeFull();
			this.rootLayout.addComponent(this.rootPanel);
			this.rootLayout.setSizeFull();
			this.rootLayout.setComponentAlignment(this.rootPanel, Alignment.MIDDLE_RIGHT);
		}

		protected void style() {
			styleName();
			styleEmail();
			stylePassword();
			styleDateOfBirth();
			styleGender();
		}

		protected void styleName() {

			this.firstName.addStyleNames(ValoTheme.TEXTFIELD_ALIGN_CENTER, ValoTheme.TEXTFIELD_INLINE_ICON,
					ValoTheme.TEXTFIELD_LARGE, ValoTheme.TEXTFIELD_BORDERLESS);

			this.middleName.addStyleNames(ValoTheme.TEXTFIELD_ALIGN_CENTER, ValoTheme.TEXTFIELD_INLINE_ICON,
					ValoTheme.TEXTFIELD_LARGE, ValoTheme.TEXTFIELD_BORDERLESS);

			this.lastName.addStyleNames(ValoTheme.TEXTFIELD_ALIGN_CENTER, ValoTheme.TEXTFIELD_INLINE_ICON,
					ValoTheme.TEXTFIELD_LARGE, ValoTheme.TEXTFIELD_BORDERLESS);

		}

		protected void styleEmail() {
			this.emailAddress.addStyleNames(ValoTheme.TEXTFIELD_ALIGN_CENTER, ValoTheme.TEXTFIELD_INLINE_ICON,
					ValoTheme.TEXTFIELD_HUGE/*
											 * , ValoTheme.TEXTFIELD_BORDERLESS
											 */);
			this.emailAddress.setSizeFull();
		}

		protected void stylePassword() {
			this.passwordField.addStyleNames(ValoTheme.TEXTFIELD_INLINE_ICON, ValoTheme.TEXTFIELD_HUGE,
					/* ValoTheme.TEXTFIELD_BORDERLESS, */ValoTheme.TEXTFIELD_ALIGN_CENTER);
			this.passwordField.setSizeFull();
			this.confirmPasswordField.addStyleNames(ValoTheme.TEXTFIELD_INLINE_ICON,
					ValoTheme.TEXTFIELD_HUGE/*
											 * , ValoTheme.TEXTFIELD_BORDERLESS
											 */, ValoTheme.TEXTFIELD_ALIGN_CENTER);
			this.confirmPasswordField.setSizeFull();
		}

		protected void styleDateOfBirth() {
			this.dateOfBirth.addStyleNames(ValoTheme.DATEFIELD_ALIGN_CENTER);
			this.dateOfBirth.setSizeFull();
		}

		protected void styleGender() {
			this.gender.addStyleNames(ValoTheme.COMBOBOX_ALIGN_CENTER);
			this.gender.setSizeFull();
		}

		protected EmptyFieldListener<?> getValueChangeListener() {
			EmptyFieldListener<?> firstNameListener = new EmptyFieldListener<>();
			firstNameListener.setMandatoryFields(
					this.username, /*
									 * this.firstName, this.middleName,
									 * this.lastName,
									 */
					this.emailAddress, this.passwordField, this.confirmPasswordField);
			// firstNameListener.setMandatoryDateFields(this.dateOfBirth);
			// firstNameListener.setMandatoryListFields(this.gender);
			firstNameListener.setTargetBtn(this.saveNext);
			return firstNameListener;
		}

		// Implement both receiver that saves upload in a file and
		// listener for successful upload
		@SuppressWarnings("unused")
		public static class ImageUploader implements Receiver, SucceededListener {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			public File file;
			public Image image;

			public ImageUploader(Image image) {
				this.image = image;
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
				this.image.setSource(new FileResource(file));
			}
		};

	}
}
