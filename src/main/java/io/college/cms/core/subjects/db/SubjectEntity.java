package io.college.cms.core.subjects.db;

import io.college.cms.core.TableNames;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = TableNames.SUBJECT_TABLE)
public class SubjectEntity {

	/**
	 * @param subjectName
	 */
	public SubjectEntity(String subjectName) {
		super();
		this.subjectName = subjectName;
	}

	/**
	 * 
	 */
	public SubjectEntity() {
		super();
	}

	@DynamoDBHashKey(attributeName = "subject_name")
	private String subjectName;

	@DynamoDBAttribute(attributeName = "theory")
	private Double theory = 0.0;
	@DynamoDBAttribute(attributeName = "practical")
	private Double practical = 0.0;
	@DynamoDBAttribute(attributeName = "internal")
	private Double internal = 0.0;
	@DynamoDBAttribute(attributeName = "others")
	private Double others = 0.0;
	@DynamoDBAttribute
	private Double total = 0.0;
	@DynamoDBAttribute(attributeName = "theory_marks_required")
	private Double theoryMarksRequired = 0.0;
	@DynamoDBAttribute(attributeName = "practical_marks_required")
	private Double practicalMarksRequired = 0.0;
	@DynamoDBAttribute(attributeName = "internal_marks_required")
	private Double internalMarksRequired = 0.0;
	@DynamoDBAttribute(attributeName = "others_marks_required")
	private Double othersMarksRequired = 0.0;
	@DynamoDBAttribute(attributeName = "show_theory")
	private boolean showTheory;
	@DynamoDBAttribute(attributeName = "show_practical")
	private boolean showPractical;
	@DynamoDBAttribute(attributeName = "show_internal")
	private boolean showInternal;
	@DynamoDBAttribute(attributeName = "show_others")
	private boolean showOthers;

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public Double getTheory() {
		return theory;
	}

	public void setTheory(Double theory) {
		this.theory = theory;
	}

	public Double getPractical() {
		return practical;
	}

	public void setPractical(Double practical) {
		this.practical = practical;
	}

	public Double getInternal() {
		return internal;
	}

	public void setInternal(Double internal) {
		this.internal = internal;
	}

	public Double getOthers() {
		return others;
	}

	public void setOthers(Double others) {
		this.others = others;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public Double getTheoryMarksRequired() {
		return theoryMarksRequired;
	}

	public void setTheoryMarksRequired(Double theoryMarksRequired) {
		this.theoryMarksRequired = theoryMarksRequired;
	}

	public Double getPracticalMarksRequired() {
		return practicalMarksRequired;
	}

	public void setPracticalMarksRequired(Double practicalMarksRequired) {
		this.practicalMarksRequired = practicalMarksRequired;
	}

	public Double getInternalMarksRequired() {
		return internalMarksRequired;
	}

	public void setInternalMarksRequired(Double internalMarksRequired) {
		this.internalMarksRequired = internalMarksRequired;
	}

	public Double getOthersMarksRequired() {
		return othersMarksRequired;
	}

	public void setOthersMarksRequired(Double othersMarksRequired) {
		this.othersMarksRequired = othersMarksRequired;
	}

	public boolean isShowTheory() {
		return showTheory;
	}

	public void setShowTheory(boolean showTheory) {
		this.showTheory = showTheory;
	}

	public boolean isShowPractical() {
		return showPractical;
	}

	public void setShowPractical(boolean showPractical) {
		this.showPractical = showPractical;
	}

	public boolean isShowInternal() {
		return showInternal;
	}

	public void setShowInternal(boolean showInternal) {
		this.showInternal = showInternal;
	}

	public boolean isShowOthers() {
		return showOthers;
	}

	public void setShowOthers(boolean showOthers) {
		this.showOthers = showOthers;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SubjectEntity [subjectName=");
		builder.append(subjectName);
		builder.append(", version=");

		builder.append(", theory=");
		builder.append(theory);
		builder.append(", practical=");
		builder.append(practical);
		builder.append(", internal=");
		builder.append(internal);
		builder.append(", others=");
		builder.append(others);
		builder.append(", total=");
		builder.append(total);
		builder.append(", theoryMarksRequired=");
		builder.append(theoryMarksRequired);
		builder.append(", practicalMarksRequired=");
		builder.append(practicalMarksRequired);
		builder.append(", internalMarksRequired=");
		builder.append(internalMarksRequired);
		builder.append(", othersMarksRequired=");
		builder.append(othersMarksRequired);
		builder.append(", showTheory=");
		builder.append(showTheory);
		builder.append(", showPractical=");
		builder.append(showPractical);
		builder.append(", showInternal=");
		builder.append(showInternal);
		builder.append(", showOthers=");
		builder.append(showOthers);
		builder.append("]");
		return builder.toString();
	}

}
