package io.college.cms.core.application.automation;

public class AutomationConstants {
	public static final String CLASS_DECLARATION = "@Slf4j @Service public class ";
	public static final String CURLY_BRACKET_START = "{";
	public static final String CURLY_BRACKET_END = "}";
	public static final String RETURN_TYPE_FACTORY_RESPONSE = "public FactoryResponse ";
	public static final String PACKAGE_SYNTAX = "package ";
	public static final String SEMI_COLON = ";";
	public static final String DEFAULT_IMPORTS = "import javax.servlet.http.HttpServletRequest;import org.springframework.stereotype.Service;import org.springframework.web.bind.annotation.RequestMapping;import org.springframework.web.bind.annotation.RestController;import io.college.cms.core.application.FactoryResponse;import io.college.cms.core.application.SummaryMessageEnum;import io.college.cms.core.exception.ApplicationException;import io.college.cms.core.exception.ExceptionHandler;import io.college.cms.core.exception.ResourceDeniedException;import io.college.cms.core.exception.ValidationException;import io.college.cms.core.user.model.UserModel;import lombok.experimental.var;import lombok.extern.slf4j.Slf4j;";
}
