package io.college.cms.core.user.constants;

import io.college.cms.core.exception.ValidationException;

public enum UserAttributes {
	CUSTOM_USER_TYPE("custom:user_type"), EMAIL("email"), PHONE_NUMBER("phone_number"), PROFILE("profile"), ADDRESS(
			"address"), BIRTH_DATE("birthdate"), FAMILY_NAME("family_name"), GIVEN_NAME("given_name"), LOCALE(
					"locale"), MIDDLE_NAME("middle_name"), PICTURE("picture"), NAME("name"), GENDER("gender");
	private String val;

	UserAttributes(String val) {
		this.val = val;
	}

	public String val() {
		return this.val;
	}

	public static UserAttributes findByVal(String val) throws ValidationException {
		UserAttributes userAttribute = null;
		for (UserAttributes attribute : values()) {
			if (!attribute.val().equalsIgnoreCase(val)) {
				continue;
			}
			userAttribute = attribute;
			break;
		}
		return userAttribute;
	}

}
