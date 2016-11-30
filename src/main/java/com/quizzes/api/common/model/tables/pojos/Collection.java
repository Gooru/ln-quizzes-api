/**
 * This class is generated by jOOQ
 */
package com.quizzes.api.common.model.tables.pojos;

/**
 * This class is generated by jOOQ.
 */
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Collection implements java.io.Serializable {

	private static final long serialVersionUID = -2052611300;

	private java.util.UUID                         id;
	private java.lang.String                       externalId;
	private com.quizzes.api.common.model.enums.Lms lmsId;
	private java.lang.Boolean                      isCollection;
	private java.util.UUID                         ownerProfileId;
	private java.lang.String                       collectionData;
	private java.lang.Boolean                      isLocked;
	private java.lang.Boolean                      isDeleted;
	private java.sql.Timestamp                     createdAt;

	public Collection() {}

	public Collection(
		java.util.UUID                         id,
		java.lang.String                       externalId,
		com.quizzes.api.common.model.enums.Lms lmsId,
		java.lang.Boolean                      isCollection,
		java.util.UUID                         ownerProfileId,
		java.lang.String                       collectionData,
		java.lang.Boolean                      isLocked,
		java.lang.Boolean                      isDeleted,
		java.sql.Timestamp                     createdAt
	) {
		this.id = id;
		this.externalId = externalId;
		this.lmsId = lmsId;
		this.isCollection = isCollection;
		this.ownerProfileId = ownerProfileId;
		this.collectionData = collectionData;
		this.isLocked = isLocked;
		this.isDeleted = isDeleted;
		this.createdAt = createdAt;
	}

	public java.util.UUID getId() {
		return this.id;
	}

	public void setId(java.util.UUID id) {
		this.id = id;
	}

	public java.lang.String getExternalId() {
		return this.externalId;
	}

	public void setExternalId(java.lang.String externalId) {
		this.externalId = externalId;
	}

	public com.quizzes.api.common.model.enums.Lms getLmsId() {
		return this.lmsId;
	}

	public void setLmsId(com.quizzes.api.common.model.enums.Lms lmsId) {
		this.lmsId = lmsId;
	}

	public java.lang.Boolean getIsCollection() {
		return this.isCollection;
	}

	public void setIsCollection(java.lang.Boolean isCollection) {
		this.isCollection = isCollection;
	}

	public java.util.UUID getOwnerProfileId() {
		return this.ownerProfileId;
	}

	public void setOwnerProfileId(java.util.UUID ownerProfileId) {
		this.ownerProfileId = ownerProfileId;
	}

	public java.lang.String getCollectionData() {
		return this.collectionData;
	}

	public void setCollectionData(java.lang.String collectionData) {
		this.collectionData = collectionData;
	}

	public java.lang.Boolean getIsLocked() {
		return this.isLocked;
	}

	public void setIsLocked(java.lang.Boolean isLocked) {
		this.isLocked = isLocked;
	}

	public java.lang.Boolean getIsDeleted() {
		return this.isDeleted;
	}

	public void setIsDeleted(java.lang.Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public java.sql.Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(java.sql.Timestamp createdAt) {
		this.createdAt = createdAt;
	}
}
