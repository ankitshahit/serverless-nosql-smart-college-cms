package io.college.cms.core.identity.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClient;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;

//test class for now need to change this.
public class UserService {

	public static void main(String[] args) {
		String accessKey = "";
		String secretKey = "";
		AWSCognitoIdentityProviderClient.builder();
		AWSCognitoIdentityProvider identityUserPoolProviderClient = AWSCognitoIdentityProviderClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
				.withRegion(Regions.AP_SOUTH_1).build();
		AdminGetUserRequest getUserRequest = new AdminGetUserRequest();
		getUserRequest.setUserPoolId("ap-south-1:8a10d71d-8bf3-4103-ad7a-2127f25518e9");
		getUserRequest.setUsername("ankitshahit");
		AdminGetUserResult result = identityUserPoolProviderClient.adminGetUser(getUserRequest);
		System.out.println(result.getUserAttributes());
	}
}
