package io.college.cms.core.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.teemusa.sidemenu.SideMenu;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Composite;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import io.college.cms.core.announcement.ui.PublishAnnouncementView;
import io.college.cms.core.announcement.ui.SeeAnnouncementView;
import io.college.cms.core.courses.controller.SeeCoursesView;
import io.college.cms.core.examination.controller.PublishExamView;
import io.college.cms.core.examination.controller.SeeExamsView;
import io.college.cms.core.examination.controller.SeeResultsView;
import io.college.cms.core.faq.controller.ChatFaqView;
import io.college.cms.core.ui.model.ViewConstants;

@SpringUI(path = "/homepage")
@UIScope
@PushStateNavigation
public class HomePageUI extends UI {

	private static final long serialVersionUID = 1L;
	@Deprecated
	private ViewAllCoursesUI viewCourses;
	private Navigator navigator;
	private CreateCourseView createCourse;
	private PublishAnnouncementView publishAnnouncement;
	private SeeAnnouncementView seeAnnouncement;
	private SeeResultsView seeResults;
	private SeeCoursesView seeCourses;
	private PublishExamView publishExam;
	private SeeExamsView seeExam;
	private ChatFaqView chatFaq;
	private SideMenu sideMenu = new SideMenu();
	private CssLayout cssContainer;

	public HomePageUI() {
		this.cssContainer = new CssLayout();
		this.navigator = new Navigator(this, cssContainer);
	}

	@Autowired
	public void setChatFaq(ChatFaqView chatFaq) {
		this.chatFaq = chatFaq;
	}

	@Autowired
	public void setSeeExam(SeeExamsView seeExam) {
		this.seeExam = seeExam;
	}

	@Autowired
	public void setPublishExam(PublishExamView publishExam) {
		this.publishExam = publishExam;
	}

	@Autowired
	public void setSeeCourses(SeeCoursesView seeCourses) {
		this.seeCourses = seeCourses;
	}

	@Autowired
	public void setSeeResults(SeeResultsView seeResults) {
		this.seeResults = seeResults;
	}

	@Autowired
	public void setSeeAnnouncement(SeeAnnouncementView seeAnnouncement) {
		this.seeAnnouncement = seeAnnouncement;
	}

	@Autowired
	public void setViewCourses(ViewAllCoursesUI viewCourses) {
		this.viewCourses = viewCourses;
	}

	@Autowired
	public void setCreateCourse(CreateCourseView createCourse) {
		this.createCourse = createCourse;
	}

	@Autowired
	public void setPublishAnnouncement(PublishAnnouncementView publishAnnouncement) {
		this.publishAnnouncement = publishAnnouncement;
	}

	@Override
	protected void init(VaadinRequest request) {
		registerViews();
		sideMenus();

		setContent(sideMenuVaadin8());
		// setContent(sideMenuVaadin8());

	}

	void registerViews() {
		// addView("", createCourse);
		addView(ViewConstants.COURSES_CREATE, createCourse);
		addView(ViewConstants.COURSES_VIEW_ALL, seeCourses);
		addView(ViewConstants.PUBLISH_ANNOUNCEMENT, publishAnnouncement);
		addView(ViewConstants.SEE_ANNOUNCEMENT, seeAnnouncement);
		addView(ViewConstants.SEE_RESULTS, seeResults);
		addView(ViewConstants.EXAM_CREATE, publishExam);
		addView(ViewConstants.EXAM_VIEW_ALL, seeExam);
		addView(ViewConstants.CHAT_FAQ_VIEW, chatFaq);
		addView("Testing", new ExampleView());
	}

	void addView(String viewName, View view) {
		this.navigator.addView(viewName, view);
	}

	HorizontalLayout sideMenuVaadin8() {
		Label title = new Label("College CMS");
		title.setStyleName(ValoTheme.MENU_TITLE);

		Button exams = new Button("Exams");
		exams.addStyleNames(ValoTheme.BUTTON_BORDERLESS, ValoTheme.MENU_ITEM);
		exams.addClickListener(click -> {
			navigator.navigateTo(ViewConstants.EXAM_VIEW_ALL);
		});
		Button courses = new Button("Courses");
		courses.addStyleNames(ValoTheme.BUTTON_BORDERLESS, ValoTheme.MENU_ITEM);
		courses.addClickListener(click -> {
			// setContent(createCourse);
			navigator.navigateTo(ViewConstants.COURSES_CREATE);
		});
		Button faqBot = new Button("FAQ Bot");
		faqBot.addStyleNames(ValoTheme.BUTTON_BORDERLESS_COLORED, ValoTheme.MENU_ITEM);
		faqBot.addClickListener(click -> {
			navigator.navigateTo(ViewConstants.CHAT_FAQ_VIEW);
		});

		CssLayout menu = new CssLayout(title, exams, courses, faqBot);
		menu.addStyleNames(ValoTheme.MENU_ROOT);
		menu.setResponsive(true);
		menu.setSizeFull();
		cssContainer.setResponsive(true);
		cssContainer.setWidth("90%");
		HorizontalSplitPanel panel = new HorizontalSplitPanel(menu, cssContainer);

		panel.setSplitPosition(9.05F, Unit.PERCENTAGE, false);
		HorizontalLayout menuLayout = new HorizontalLayout();
		menuLayout.addComponents(panel);
		menuLayout.setSizeFull();
		menuLayout.setResponsive(true);
		return menuLayout;
	}

	static class ExampleView extends Composite implements View {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ExampleView() {
			VerticalLayout layout = new VerticalLayout();
			Label lbl = new Label("adf");
			layout.addComponent(lbl);
			layout.setComponentAlignment(lbl, Alignment.TOP_LEFT);
			layout.setSizeFull();
			setCompositionRoot(layout);
		}

		@Override
		public void enter(ViewChangeEvent event) {
			View.super.enter(event);
		}

		@Override
		public void beforeLeave(ViewBeforeLeaveEvent event) {
			View.super.beforeLeave(event);
		}
	}

	void sideMenus() {
		sideMenu.addMenuItem("Help!", VaadinIcons.COG, () -> {

		});

		sideMenu.addMenuItem("Ask FAQ Bot", VaadinIcons.CLIPBOARD_USER, () -> {
			sideMenu.setContent(chatFaq);
		});
		sideMenu.addMenuItem("Chat with others!", () -> {

		});
		sideMenu.addMenuItem("Exams", () -> {

		});
		sideMenu.addMenuItem("Schedule an Exam", () -> {
			sideMenu.setContent(publishExam);
			// navigator.navigateTo(ViewConstants.EXAM_CREATE);
		});
		sideMenu.addMenuItem("View/Modify Exams", () -> {
			sideMenu.setContent(seeExam);
			// navigator.navigateTo(ViewConstants.EXAM_VIEW_ALL);
		});

		// User menu controls
		sideMenu.addMenuItem("Show/Hide user menu", VaadinIcons.USER,
				() -> sideMenu.setUserMenuVisible(!sideMenu.isUserMenuVisible()));

	}

}
