package io.college.cms.core.application.automation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;

import org.apache.commons.collections.CollectionUtils;

import io.college.cms.core.application.Utils;
import io.college.cms.core.application.automation.FactoryResponseParams.FactoryResponseParamsBuilder;
import io.college.cms.core.exception.ApplicationException;
import lombok.Cleanup;

public class GenerateService {
	private static final String BODY_BLUEPRINT_LOCATION = "F:\\workspace\\college.io\\src\\main\\resources\\exceptionhandling.jbp";
	private static final String FILE_CREATION_LOCATION = "F:\\workspace\\college.io\\src\\main\\resources\\UserService.jbp";

	public static void main(String[] args) throws FileNotFoundException, ApplicationException {
		FactoryResponseParamsBuilder builder = FactoryResponseParams.builder();
		StringBuilder writeFileTo = new StringBuilder();
		@Cleanup
		Scanner sc = new Scanner(new File(FILE_CREATION_LOCATION));
		System.out.println("provide location ");
		writeFileTo.append(sc.nextLine());
		System.out.println("Provide packagename");
		builder.packageName(sc.nextLine());

		System.out.println("provide className");
		builder.className(sc.nextLine());
		System.out.println("provide total methods to be created");
		int max = Integer.parseInt(sc.nextLine());
		System.out.println("enter methods with their method signatures.");
		for (int index = 0; index < max; index++) {
			builder.method(sc.nextLine());
		}
		System.out.println("Enter imports to be provided number");
		max = Integer.parseInt(sc.nextLine());
		for (int index = 0; index < max; index++) {
			builder.importLine(sc.nextLine());
		}
		System.out.println(builder.build());

		createFile(build(builder.build()), writeFileTo.toString());

	}

	public static String build(FactoryResponseParams params) {
		StringBuilder sourceFile = new StringBuilder();
		sourceFile.append(AutomationConstants.PACKAGE_SYNTAX).append(params.getPackageName())
				.append(AutomationConstants.SEMI_COLON);
		if (CollectionUtils.isNotEmpty(params.getImports())) {
			params.getImports().forEach(action -> {
				sourceFile.append(action);
			});
		}

		String body = Utils.readFileAsString(BODY_BLUEPRINT_LOCATION);
		sourceFile.append(AutomationConstants.CLASS_DECLARATION).append(params.getClassName())
				.append(AutomationConstants.CURLY_BRACKET_START);
		if (CollectionUtils.isNotEmpty(params.getMethods())) {
			params.getMethods().forEach(action -> {
				sourceFile.append(AutomationConstants.RETURN_TYPE_FACTORY_RESPONSE).append(action).append(body);
			});
		}
		sourceFile.append(AutomationConstants.CURLY_BRACKET_END);
		return sourceFile.toString();
	}

	public static void createFile(String sourceCode, String path) throws ApplicationException {
		try {
			@Cleanup
			Writer writer = new FileWriter(new File(path));
			writer.write(sourceCode);

			System.out.println(new StringBuilder().append("created? ").append(new File(path).exists()));
		} catch (IOException e) {
			throw new ApplicationException(e.getMessage());
		}
	}
}
