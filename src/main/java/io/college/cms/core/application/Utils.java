package io.college.cms.core.application;

import java.io.File;
import java.util.Optional;
import java.util.Scanner;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Utils {
	public static String readFileAsString(String path) {
		StringBuilder sb = new StringBuilder();
		try {
			@Cleanup
			Scanner sc = new Scanner(new File(path));
			while (sc.hasNextLine()) {
				sb.append(sc.nextLine());
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return sb.toString();
	}

	public static String val(Optional<String> val) {
		if (!val.isPresent()) {
			return "";
		}
		return val.get();
	}

	public static String val(AbstractField<?> field) {
		if (field != null && field.getOptionalValue().isPresent()) {
			return String.valueOf(field.getValue());
		}
		return "";
	}

	public static Double doubleVal(AbstractField<?> field) {
		if (field != null && field.getOptionalValue().isPresent()) {
			return Double.valueOf(String.valueOf(field.getOptionalValue().get()));
		}
		return 0.0;
	}

	public static Notification showErrorNotification(String description) {
		Notification notifi = Notification.show("", Type.ERROR_MESSAGE);
		notifi.setDelayMsec(Notification.DELAY_FOREVER);
		notifi.setCaption("Error");
		notifi.setDescription(description);
		notifi.setIcon(VaadinIcons.STOP);
		return notifi;
	}

	// TODO: refactor the method
	public static Notification showFactoryResponseMsg(FactoryResponse fr) {
		return showFactoryResponseMsg(fr, String.valueOf(fr.getResponse()), String.valueOf(fr.getResponse()));
	}

	public static Notification showFactoryResponseMsg(FactoryResponse fr, Notification.CloseListener closeListener) {
		if (fr == null || SummaryMessageEnum.SUCCESS != fr.getSummaryMessage()) {
			Notification notifi = Notification.show("", Type.ERROR_MESSAGE);
			notifi.setDelayMsec(Notification.DELAY_FOREVER);
			notifi.setCaption("Error");
			notifi.setDescription(String.valueOf(fr.getResponse()));
			notifi.setIcon(VaadinIcons.STOP);
			notifi.addCloseListener(closeListener);
			return notifi;
		} else {
			Notification notifi = Notification.show("", Type.HUMANIZED_MESSAGE);
			notifi.setDelayMsec(Notification.DELAY_FOREVER);
			notifi.setCaption("Success");
			notifi.setDescription(String.valueOf(fr.getResponse()));
			notifi.setIcon(VaadinIcons.CHECK);
			notifi.addCloseListener(closeListener);
			return notifi;
		}
	}

	public static Notification showFactoryResponseMsg(FactoryResponse fr, String errorMsg, String successMsg) {
		if (fr == null || SummaryMessageEnum.SUCCESS != fr.getSummaryMessage()) {
			Notification notifi = Notification.show("", Type.ERROR_MESSAGE);
			notifi.setDelayMsec(Notification.DELAY_FOREVER);
			notifi.setCaption("Error");
			notifi.setDescription(errorMsg);
			notifi.setIcon(VaadinIcons.STOP);
			return notifi;
		} else {
			Notification notifi = Notification.show("", Type.HUMANIZED_MESSAGE);
			notifi.setDelayMsec(Notification.DELAY_FOREVER);
			notifi.setCaption("Success");
			notifi.setDescription(successMsg);
			notifi.setIcon(VaadinIcons.CHECK);
			return notifi;
		}
	}

	public static void showFactoryResponseOnlyError(FactoryResponse fr) {
		if (fr == null || SummaryMessageEnum.SUCCESS != fr.getSummaryMessage()) {
			Notification notifi = Notification.show("", Type.ERROR_MESSAGE);
			notifi.setDelayMsec(Notification.DELAY_FOREVER);
			notifi.setCaption("Error");
			notifi.setDescription(String.valueOf(fr.getResponse()));
			notifi.setIcon(VaadinIcons.STOP);
		}
		return;
	}

	public static boolean isError(FactoryResponse fr) {
		return fr == null || SummaryMessageEnum.SUCCESS != fr.getSummaryMessage();
	}

	public static boolean isSuccess(FactoryResponse fr) {
		return !isError(fr);
	}

	public static boolean isNull(Object val) {
		return val == null;
	}

	public static boolean isNotNull(Object val) {
		return !isNull(val);
	}
}
