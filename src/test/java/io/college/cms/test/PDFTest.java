package io.college.cms.test;

import java.util.ArrayList;
import java.util.List;

import io.college.cms.core.configuration.AppParams;
import io.college.cms.core.courses.controller.constants.SubjectType;
import io.college.cms.core.examination.service.ExamQrService;
import io.college.cms.core.exception.ApplicationException;

public class PDFTest {

	public static void main(String[] args) throws IllegalArgumentException, ApplicationException {
		ExamQrService qr = new ExamQrService(new AppParams());
		List<String> strings = new ArrayList<>();
		for (int index = 0; index < 10; index++) {
			strings.add("bhai bhai" + index + "");
		}
		System.out.println(qr.printForSubject("exam", "subject", SubjectType.THEORY, strings).getAbsolutePath());
	}
}
