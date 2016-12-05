/**
 * This class is generated by jOOQ
 */
package com.quizzes.api.common.model.jooq.tables.pojos;

/**
 * This class is generated by jOOQ.
 */
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Context implements java.io.Serializable {

	private static final long serialVersionUID = -103238090;

	private java.util.UUID     id;
	private java.util.UUID     groupId;
	private java.util.UUID     collectionId;
	private java.lang.String   contextData;
	private java.lang.Boolean  isDeleted;
	private java.sql.Timestamp createdAt;
	private java.sql.Timestamp updatedAt;

	public Context() {}

	public Context(
		java.util.UUID     id,
		java.util.UUID     groupId,
		java.util.UUID     collectionId,
		java.lang.String   contextData,
		java.lang.Boolean  isDeleted,
		java.sql.Timestamp createdAt,
		java.sql.Timestamp updatedAt
	) {
		this.id = id;
		this.groupId = groupId;
		this.collectionId = collectionId;
		this.contextData = contextData;
		this.isDeleted = isDeleted;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public java.util.UUID getId() {
		return this.id;
	}

	public void setId(java.util.UUID id) {
		this.id = id;
	}

	public java.util.UUID getGroupId() {
		return this.groupId;
	}

	public void setGroupId(java.util.UUID groupId) {
		this.groupId = groupId;
	}

	public java.util.UUID getCollectionId() {
		return this.collectionId;
	}

	public void setCollectionId(java.util.UUID collectionId) {
		this.collectionId = collectionId;
	}

	public java.lang.String getContextData() {
		return this.contextData;
	}

	public void setContextData(java.lang.String contextData) {
		this.contextData = contextData;
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

	public java.sql.Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(java.sql.Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}
}