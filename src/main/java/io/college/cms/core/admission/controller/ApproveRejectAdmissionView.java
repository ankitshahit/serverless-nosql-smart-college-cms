package io.college.cms.core.admission.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.data.HasValue;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.admission.model.ApplyAdmissionModel;
import io.college.cms.core.admission.services.AdmissionResponseService;
import io.college.cms.core.application.FactoryResponse;
import io.college.cms.core.application.SummaryMessageEnum;
import io.college.cms.core.application.Utils;
import io.college.cms.core.courses.db.CourseModel;
import io.college.cms.core.courses.service.CourseResponseService;
import io.college.cms.core.ui.builder.MessagePopupView;
import io.college.cms.core.ui.builder.VaadinWrapper;
import io.college.cms.core.ui.services.CoreUiService;
import io.college.cms.core.ui.util.ListenerUtility;
import io.college.cms.core.user.service.SecurityService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class ApproveRejectAdmissionView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;
	private CoreUiService coreUi;

	private Grid<ApplyAdmissionModel> gridAdmissionModel;
	private ComboBox<String> courseNamesCb;
	private SecurityService securityService;
	private CourseResponseService courseResponseService;
	private AdmissionResponseService admissionResponseService;
	@Setter
	private ApplyAdmissionModel applyAdmissionModel;
	private TextField filterByUsernameFld;
	private TextField filterByCourseFld;

	/**
	 * @param coreUi
	 * @param securityService
	 * @param courseResponseService
	 * @param admissionResponseService
	 */
	@Autowired
	public ApproveRejectAdmissionView(CoreUiService coreUi, SecurityService securityService,
			CourseResponseService courseResponseService, AdmissionResponseService admissionResponseService) {
		super();
		this.coreUi = coreUi;
		this.securityService = securityService;
		this.courseResponseService = courseResponseService;
		this.admissionResponseService = admissionResponseService;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		// coreUi.setItemsCourseNames(this.courseNamesCb);
		try {
			FactoryResponse fr = admissionResponseService.findAllAdmissions();
			if (Utils.isError(fr)) {
				Utils.showFactoryResponseMsg(fr);
				return;
			}
			List<ApplyAdmissionModel> records = (List<ApplyAdmissionModel>) fr.getResponse();
			gridAdmissionModel.setItems(records);

		} catch (Exception ex) {

		}
	}

	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		View.super.beforeLeave(event);
	}

	@PostConstruct
	protected void paint() {
		this.courseNamesCb = coreUi.getCoursesList();
		this.gridAdmissionModel = new Grid<>();
		this.filterByUsernameFld = VaadinWrapper.builder().caption("Filter by username").placeholder("type username")
				.icon(VaadinIcons.SEARCH).build().textField();
		this.filterByCourseFld = VaadinWrapper.builder().caption("Filter by coursename").placeholder("type coursename")
				.icon(VaadinIcons.SEARCH).build().textField();

		this.gridAdmissionModel.addColumn(ApplyAdmissionModel::getUsername).setCaption("Student username");
		this.gridAdmissionModel.addColumn(ApplyAdmissionModel::getCourseName).setCaption("Course name");
		this.gridAdmissionModel.addColumn(ApplyAdmissionModel::getAppliedOn).setCaption("Applied on");
		this.gridAdmissionModel.addColumn(ApplyAdmissionModel::isFeesVerficationDone)
				.setCaption("Fees verification done?");
		this.gridAdmissionModel.addColumn(ApplyAdmissionModel::isFeesVerificationReceiptRequired)
				.setCaption("Fees verification required?");
		this.gridAdmissionModel.addColumn(ApplyAdmissionModel::isRejected).setCaption("Rejected?");
		this.gridAdmissionModel.addColumn(ApplyAdmissionModel::isApproved).setCaption("Approved?");
		this.gridAdmissionModel.addColumn(ApplyAdmissionModel::getComments).setCaption("Comments");
		this.gridAdmissionModel.setSizeFull();
		this.gridAdmissionModel.addItemClickListener(click -> {

			if (click.getItem() == null) {
				return;
			}
			Window window = new Window();
			window.setResizable(false);
			window.center();
			VerticalLayout layout = new VerticalLayout();
			Label username = coreUi.getLabel("Student username");
			username.setValue(click.getItem().getUsername());
			Label courseNameLbl = coreUi.getLabel("Course name");
			courseNameLbl.setValue(click.getItem().getCourseName());
			courseNameLbl.setSizeFull();
			RadioButtonGroup<String> radioOption = new RadioButtonGroup<String>();
			radioOption.setCaption("<b>Application status</b>");
			radioOption.setCaptionAsHtml(true);
			radioOption.setItems("Approve", "Reject");
			RichTextArea textArea = VaadinWrapper.builder().caption("Comments").required(false).build().richTextArea();
			Button button = VaadinWrapper.builder().enabled(false).build().button();
			button.setCaption("Save");
			button.addClickListener(btnClick -> {
				ApplyAdmissionModel model = click.getItem();

				model.setApproved("approve".equalsIgnoreCase(radioOption.getOptionalValue().get()));
				model.setRejected(!"approve".equalsIgnoreCase(radioOption.getOptionalValue().get()));
				model.setComments(Utils.val(textArea));
				model.setActionBy(securityService.getPrincipal());
				FactoryResponse fr = admissionResponseService.saveUpdate(model);
				Utils.showFactoryResponseOnlyError(fr);
				if (fr == null || SummaryMessageEnum.SUCCESS != fr.getSummaryMessage()) {
					return;
				}
				if (model.isApproved() && !model.isFeesVerificationReceiptRequired()) {
					fr = courseResponseService.findByCourseName(null, model.getCourseName());
					Utils.showFactoryResponseOnlyError(fr);
					if (fr == null || SummaryMessageEnum.SUCCESS != fr.getSummaryMessage()) {
						return;
					}

					CourseModel course = (CourseModel) fr.getResponse();
					if (course.getEnrolledStudents() + 1 > course.getMaxStudentsAllowed()) {
						Utils.showErrorNotification("Course cannot enroll more students, all seats are allocated!");
						return;
					} else {
						course.setEnrolledStudents(course.getEnrolledStudents() + 1);
					}
					// TODO: need to add for each and every subject that is
					// selected by student.
					List<String> users = course.getUsers();
					if (CollectionUtils.isEmpty(users)) {
						users = new ArrayList<>();
					}
					users.add(model.getUsername());
					course.setUsers(users);
					fr = courseResponseService.saveCourseMetadata(course);
					Utils.showFactoryResponseMsg(fr);
					if (fr == null || SummaryMessageEnum.SUCCESS != fr.getSummaryMessage()) {
						return;
					}
				} else {
					MessagePopupView message = new MessagePopupView("Success!",
							"Complete fees payment and verification for confirmation of admission enrollment!", 50.0f);
					message.center();
					getUI().addWindow(message);
				}
			});
			radioOption.addValueChangeListener(value -> {
				if (!ListenerUtility.isValidSourceEvent(value.getComponent(), radioOption)) {
					return;
				}
				button.setEnabled(radioOption.getOptionalValue().isPresent());
			});
			Button cancel = VaadinWrapper.builder().build().button();
			cancel.setCaption("Close");
			cancel.setStyleName(ValoTheme.BUTTON_QUIET);
			cancel.addClickListener(close -> {
				if (!ListenerUtility.isValidSourceEvent(close.getComponent(), cancel)) {
					return;
				}
				window.close();
			});
			HorizontalLayout buttonLayout = new HorizontalLayout(cancel, button);

			layout.addComponents(new Panel(new VerticalLayout(courseNameLbl, username, radioOption, textArea)),
					buttonLayout);
			layout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_RIGHT);

			window.setContent(layout);
			getUI().addWindow(window);
		});

		this.filterByCourseFld.addValueChangeListener(this::onCourseFilterTextChange);
		this.filterByUsernameFld.addValueChangeListener(this::onUsernameFilterTextChange);
		VerticalLayout rootLayout = new VerticalLayout();
		Panel rootPanel = new Panel();
		rootPanel.setContent(this.gridAdmissionModel);
		HorizontalLayout searchLayout = new HorizontalLayout(this.filterByUsernameFld, this.filterByCourseFld);
		searchLayout.setSizeFull();
		rootLayout.addComponents(new Panel(new VerticalLayout(searchLayout, rootPanel)));

		Panel designPanel = new Panel();
		designPanel.setContent(rootLayout);
		addComponent(rootLayout);
	}

	private void onUsernameFilterTextChange(HasValue.ValueChangeEvent<String> event) {
		ListDataProvider<ApplyAdmissionModel> dataProvider = (ListDataProvider<ApplyAdmissionModel>) gridAdmissionModel
				.getDataProvider();
		dataProvider.setFilter(ApplyAdmissionModel::getUsername, s -> caseInsensitiveContains(s, event.getValue()));
	}

	private void onCourseFilterTextChange(HasValue.ValueChangeEvent<String> event) {
		ListDataProvider<ApplyAdmissionModel> dataProvider = (ListDataProvider<ApplyAdmissionModel>) gridAdmissionModel
				.getDataProvider();
		dataProvider.setFilter(ApplyAdmissionModel::getCourseName, s -> caseInsensitiveContains(s, event.getValue()));
	}

	private Boolean caseInsensitiveContains(String where, String what) {
		return new StringBuilder().append("").append(where).toString().toLowerCase()
				.contains(new StringBuilder().append("").append(what).toString().toLowerCase());
	}

}
