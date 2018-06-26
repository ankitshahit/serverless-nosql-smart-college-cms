package io.college.cms.core.ui.builder;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ComboBoxWrapper {
	private String caption;
	private boolean required;
	private boolean enabled;
	private String placeholder;
	
	
}
