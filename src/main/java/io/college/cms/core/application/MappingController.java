package io.college.cms.core.application;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(path = "/test")
public class MappingController {
	@RequestMapping(method = RequestMethod.GET)
	public String getIndexPage() {
		return "/homepage";
	}
}
