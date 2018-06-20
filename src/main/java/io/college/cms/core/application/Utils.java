package io.college.cms.core.application;

import java.io.File;
import java.util.Optional;
import java.util.Scanner;

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
}
