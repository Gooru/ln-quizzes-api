/**
 * This class is generated by jOOQ
 */
package com.quizzes.api.common.model.jooq.tables.records;

/**
 * This class is generated by jOOQ.
 */
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class GroupProfileRecord extends org.jooq.impl.TableRecordImpl<com.quizzes.api.common.model.jooq.tables.records.GroupProfileRecord> implements org.jooq.Record4<java.util.UUID, java.util.UUID, java.util.UUID, java.sql.Timestamp> {

	private static final long serialVersionUID = -1850382804;

	/**
	 * Setter for <code>public.group_profile.id</code>.
	 */
	public void setId(java.util.UUID value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>public.group_profile.id</code>.
	 */
	public java.util.UUID getId() {
		return (java.util.UUID) getValue(0);
	}

	/**
	 * Setter for <code>public.group_profile.group_id</code>.
	 */
	public void setGroupId(java.util.UUID value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>public.group_profile.group_id</code>.
	 */
	public java.util.UUID getGroupId() {
		return (java.util.UUID) getValue(1);
	}

	/**
	 * Setter for <code>public.group_profile.profile_id</code>.
	 */
	public void setProfileId(java.util.UUID value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>public.group_profile.profile_id</code>.
	 */
	public java.util.UUID getProfileId() {
		return (java.util.UUID) getValue(2);
	}

	/**
	 * Setter for <code>public.group_profile.created_at</code>.
	 */
	public void setCreatedAt(java.sql.Timestamp value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>public.group_profile.created_at</code>.
	 */
	public java.sql.Timestamp getCreatedAt() {
		return (java.sql.Timestamp) getValue(3);
	}

	// -------------------------------------------------------------------------
	// Record4 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row4<java.util.UUID, java.util.UUID, java.util.UUID, java.sql.Timestamp> fieldsRow() {
		return (org.jooq.Row4) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row4<java.util.UUID, java.util.UUID, java.util.UUID, java.sql.Timestamp> valuesRow() {
		return (org.jooq.Row4) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.util.UUID> field1() {
		return com.quizzes.api.common.model.jooq.tables.GroupProfile.GROUP_PROFILE.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.util.UUID> field2() {
		return com.quizzes.api.common.model.jooq.tables.GroupProfile.GROUP_PROFILE.GROUP_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.util.UUID> field3() {
		return com.quizzes.api.common.model.jooq.tables.GroupProfile.GROUP_PROFILE.PROFILE_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.sql.Timestamp> field4() {
		return com.quizzes.api.common.model.jooq.tables.GroupProfile.GROUP_PROFILE.CREATED_AT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.UUID value1() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.UUID value2() {
		return getGroupId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.UUID value3() {
		return getProfileId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.sql.Timestamp value4() {
		return getCreatedAt();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroupProfileRecord value1(java.util.UUID value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroupProfileRecord value2(java.util.UUID value) {
		setGroupId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroupProfileRecord value3(java.util.UUID value) {
		setProfileId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroupProfileRecord value4(java.sql.Timestamp value) {
		setCreatedAt(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroupProfileRecord values(java.util.UUID value1, java.util.UUID value2, java.util.UUID value3, java.sql.Timestamp value4) {
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached GroupProfileRecord
	 */
	public GroupProfileRecord() {
		super(com.quizzes.api.common.model.jooq.tables.GroupProfile.GROUP_PROFILE);
	}

	/**
	 * Create a detached, initialised GroupProfileRecord
	 */
	public GroupProfileRecord(java.util.UUID id, java.util.UUID groupId, java.util.UUID profileId, java.sql.Timestamp createdAt) {
		super(com.quizzes.api.common.model.jooq.tables.GroupProfile.GROUP_PROFILE);

		setValue(0, id);
		setValue(1, groupId);
		setValue(2, profileId);
		setValue(3, createdAt);
	}
}