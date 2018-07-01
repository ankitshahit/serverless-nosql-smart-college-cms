package io.college.cms.core.upload.model;

import java.io.Serializable;
import java.time.LocalDate;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;

import io.college.cms.core.application.LocalDateConverter;
import io.college.cms.core.dynamodb.constants.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = Table.UPLOAD_TABLE)
public class UploadModel implements Serializable {
	private static final long serialVersionUID = 1L;
	@DynamoDBHashKey
	private String key;
	@DynamoDBAttribute(attributeName = "username")
	private String username;
	@DynamoDBAttribute(attributeName = "tag")
	private String tag;
	@Builder.Default
	@DynamoDBTypeConverted(converter = LocalDateConverter.class)
	private LocalDate uploadedOn = LocalDate.now();
	@DynamoDBAttribute(attributeName = "filename")
	private String filename;
	@DynamoDBAttribute(attributeName = "s3_bucket_link")
	private String s3BucketLink;
	@DynamoDBAttribute(attributeName = "course_name")
	private String coursename;
	@DynamoDBAttribute(attributeName = "is_group_file")
	private boolean groupFile;
}
