package io.college.cms.core.application.automation;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FactoryResponseParams {
	private String packageName;
	private String className;
	@Singular(value = "method")
	private List<String> methods;
	@Singular(value = "importLine")
	private List<String> imports;
}
