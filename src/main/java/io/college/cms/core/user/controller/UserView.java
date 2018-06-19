package io.college.cms.core.user.controller;

import java.time.LocalDate;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Composite;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.ui.builder.DeletePopupView;
import io.college.cms.core.ui.builder.TextFieldWrapper;
import io.college.cms.core.ui.util.ListenerUtility;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class UserView extends Composite implements View {

	private static final long serialVersionUID = 1L;

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

	public UserView() {
		super();
		initUI();
	}

	/**
	 * UI elements or instances need a place to be initialized, as well to avoid
	 * duplication in-case there are multiple constructor to initialize
	 * consolidating instantiating to a method will help to simply call from the
	 * constructor or postconstruct whichever is more suitable.
	 */
	protected void initUI() {
		// setting attributes for first name
		this.firstName = TextFieldWrapper.builder().placeholder("First name").enabled(true).required(true)
				.icon(VaadinIcons.USER_STAR).build().textField();
		this.firstName.setTabIndex(1);
		// setting attributes for second name
		this.middleName = TextFieldWrapper.builder().placeholder("Middle name").enabled(true).required(true)
				.icon(VaadinIcons.USER_STAR).build().textField();
		this.middleName.setTabIndex(2);
		// setting attributes for last name
		this.lastName = TextFieldWrapper.builder().placeholder("Last name").enabled(true).required(true)
				.icon(VaadinIcons.USER_STAR).build().textField();
		this.lastName.setTabIndex(3);
		// setting attributes for email address.
		this.emailAddress = TextFieldWrapper.builder().caption("Email address").enabled(true).required(true)
				.icon(VaadinIcons.USER_STAR).placeholder("example@example.org").build().textField();
		this.emailAddress.setTabIndex(4);
		this.emailAddress.setStyleName(ValoTheme.TEXTFIELD_BORDERLESS);

		// setting attributes for date field.
		this.dateOfBirth = new DateField();
		this.dateOfBirth.setRequiredIndicatorVisible(true);
		this.dateOfBirth.setPlaceholder("Date of birth");
		// TODO: need to import a better set of icons into spring
		this.dateOfBirth.setIcon(VaadinIcons.DATE_INPUT);
		this.dateOfBirth.setDateOutOfRangeMessage("Date is out of range");
		this.dateOfBirth.setResponsive(true);
		this.dateOfBirth.setRangeEnd(LocalDate.now());

		// setting attributes for gender
		this.gender = new ComboBox<String>();
		this.gender.setCaption("Gender");
		this.gender.setIcon(VaadinIcons.FEMALE);
		this.gender.setRequiredIndicatorVisible(true);

		// setting attributes for passwordfield
		this.passwordField = new PasswordField();
		this.passwordField.setCaption("New password");
		this.passwordField.setPlaceholder("Type here");
		this.passwordField.setIcon(VaadinIcons.PASSWORD);
		this.passwordField.setRequiredIndicatorVisible(true);

		// setting attributes for confirm password field policy
		this.confirmPasswordField = new PasswordField();
		this.confirmPasswordField.setCaption("Confirm password");
		this.confirmPasswordField.setPlaceholder("Type here");
		this.confirmPasswordField.setIcon(VaadinIcons.PASSWORD);
		this.confirmPasswordField.setRequiredIndicatorVisible(true);

		// no attributes are required at the moment, as there is no password
		// policy
		this.passwordPolicy = new Label("Password should be greater than or equal to 8 characters");
		// setting attributes for archive user button
		this.archiveUser = new Button("Archive");
		this.archiveUser.setStyleName(ValoTheme.BUTTON_DANGER);
		// setting attributes for save next button.
		this.saveNext = new Button("Save & Next");
		this.saveNext.setStyleName(ValoTheme.BUTTON_PRIMARY);
		this.accordin = new Accordion();

	}

	@PostConstruct
	protected void paint() {
		this.rootPanel = new Panel();
		this.rootLayout = new VerticalLayout();
		style();
		// grouping into as one layout to handle positioning of fields and
		// styling better: combining all fields related to name
		HorizontalLayout nameCssLayout = new HorizontalLayout();
		nameCssLayout.addComponents(this.firstName, this.middleName, this.lastName);

		// combining all fields related to button on step one screen
		CssLayout buttonCssLayout = new CssLayout();
		buttonCssLayout.addComponents(this.archiveUser, this.saveNext);

		// combining all fields related to password fields.
		HorizontalLayout passwordCssLayout = new HorizontalLayout();
		passwordCssLayout.addComponents(this.passwordField, this.passwordPolicy);

		// adding click listener, that we need to execute stuff on click of a
		// button in ui
		this.archiveUser.addClickListener(click -> {
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

		// setting items because we need these values to appear in a combobox.
		this.gender.setItems("Male", "Female", "Others");

		CssLayout dbAndGender = new CssLayout();
		dbAndGender.addComponents(this.dateOfBirth, this.gender);

		this.saveNext.addClickListener(click -> {
			if (!ListenerUtility.isValidSourceEvent(click.getComponent(), this.saveNext)) {
				return;
			}
		});
		GridLayout grid = new GridLayout();

		grid.addComponents(nameCssLayout, dbAndGender, this.emailAddress, this.passwordPolicy, this.passwordField,
				this.confirmPasswordField, buttonCssLayout);

		grid.setSpacing(true);
		grid.setComponentAlignment(buttonCssLayout, Alignment.MIDDLE_RIGHT);
		/*
		 * verticalLayout.setComponentAlignment(this.emailAddress,
		 * Alignment.MIDDLE_LEFT);
		 * verticalLayout.setComponentAlignment(nameCssLayout,
		 * Alignment.TOP_CENTER);
		 * verticalLayout.setComponentAlignment(passwordCssLayout,
		 * Alignment.MIDDLE_CENTER);
		 * verticalLayout.setComponentAlignment(dbAndGender,
		 * Alignment.MIDDLE_CENTER);
		 * verticalLayout.setComponentAlignment(buttonCssLayout,
		 * Alignment.MIDDLE_RIGHT);
		 */

		this.accordin.addTab(grid, "Step 1/2");
		this.accordin.addTab(new Label("Data "), "Step 2/2");
		this.rootPanel.setContent(this.accordin);
		this.rootPanel.setSizeFull();
		this.rootLayout.addComponent(this.rootPanel);
		this.rootLayout.setSizeFull();
		this.rootLayout.setComponentAlignment(this.rootPanel, Alignment.MIDDLE_RIGHT);
		setCompositionRoot(this.rootPanel);

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
		this.firstName.setCaption("&nbsp;Required");
		this.firstName.setCaptionAsHtml(true);

		this.middleName.addStyleNames(ValoTheme.TEXTFIELD_ALIGN_CENTER, ValoTheme.TEXTFIELD_INLINE_ICON,
				ValoTheme.TEXTFIELD_LARGE, ValoTheme.TEXTFIELD_BORDERLESS);

		this.lastName.addStyleNames(ValoTheme.TEXTFIELD_ALIGN_CENTER, ValoTheme.TEXTFIELD_INLINE_ICON,
				ValoTheme.TEXTFIELD_LARGE, ValoTheme.TEXTFIELD_BORDERLESS);

	}

	protected void styleEmail() {
		this.emailAddress.addStyleNames(ValoTheme.TEXTFIELD_ALIGN_CENTER, ValoTheme.TEXTFIELD_INLINE_ICON,
				ValoTheme.TEXTFIELD_LARGE, ValoTheme.TEXTFIELD_BORDERLESS);
		this.emailAddress.setSizeFull();
	}

	protected void stylePassword() {
		this.passwordField.addStyleNames(ValoTheme.TEXTFIELD_INLINE_ICON, ValoTheme.TEXTFIELD_LARGE,
				ValoTheme.TEXTFIELD_BORDERLESS, ValoTheme.TEXTFIELD_ALIGN_CENTER);
		this.passwordField.setSizeFull();
		this.confirmPasswordField.addStyleNames(ValoTheme.TEXTFIELD_INLINE_ICON, ValoTheme.TEXTFIELD_LARGE,
				ValoTheme.TEXTFIELD_BORDERLESS, ValoTheme.TEXTFIELD_ALIGN_CENTER);
		this.confirmPasswordField.setSizeFull();
	}

	protected void styleDateOfBirth() {
		this.dateOfBirth.addStyleNames(ValoTheme.DATEFIELD_BORDERLESS);
	}

	protected void styleGender() {
		this.gender.addStyleNames(ValoTheme.COMBOBOX_BORDERLESS);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		try {
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
}
