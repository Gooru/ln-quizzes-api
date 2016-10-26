/**
 * This class is generated by jOOQ
 */
package com.quizzes.api.common.model.tables.records;

/**
 * This class is generated by jOOQ.
 */
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ProfileRecord extends org.jooq.impl.TableRecordImpl<com.quizzes.api.common.model.tables.records.ProfileRecord> implements org.jooq.Record5<java.util.UUID, java.lang.String, com.quizzes.api.common.model.enums.Lms, java.lang.String, java.sql.Timestamp> {

	private static final long serialVersionUID = 1838757012;

	/**
	 * Setter for <code>public.profile.id</code>.
	 */
	public void setId(java.util.UUID value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>public.profile.id</code>.
	 */
	public java.util.UUID getId() {
		return (java.util.UUID) getValue(0);
	}

	/**
	 * Setter for <code>public.profile.external_id</code>.
	 */
	public void setExternalId(java.lang.String value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>public.profile.external_id</code>.
	 */
	public java.lang.String getExternalId() {
		return (java.lang.String) getValue(1);
	}

	/**
	 * Setter for <code>public.profile.lms_id</code>.
	 */
	public void setLmsId(com.quizzes.api.common.model.enums.Lms value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>public.profile.lms_id</code>.
	 */
	public com.quizzes.api.common.model.enums.Lms getLmsId() {
		return (com.quizzes.api.common.model.enums.Lms) getValue(2);
	}

	/**
	 * Setter for <code>public.profile.profile_data</code>.
	 */
	public void setProfileData(java.lang.String value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>public.profile.profile_data</code>.
	 */
	public java.lang.String getProfileData() {
		return (java.lang.String) getValue(3);
	}

	/**
	 * Setter for <code>public.profile.created_at</code>.
	 */
	public void setCreatedAt(java.sql.Timestamp value) {
		setValue(4, value);
	}

	/**
	 * Getter for <code>public.profile.created_at</code>.
	 */
	public java.sql.Timestamp getCreatedAt() {
		return (java.sql.Timestamp) getValue(4);
	}

	// -------------------------------------------------------------------------
	// Record5 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row5<java.util.UUID, java.lang.String, com.quizzes.api.common.model.enums.Lms, java.lang.String, java.sql.Timestamp> fieldsRow() {
		return (org.jooq.Row5) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row5<java.util.UUID, java.lang.String, com.quizzes.api.common.model.enums.Lms, java.lang.String, java.sql.Timestamp> valuesRow() {
		return (org.jooq.Row5) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.util.UUID> field1() {
		return com.quizzes.api.common.model.tables.Profile.PROFILE.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field2() {
		return com.quizzes.api.common.model.tables.Profile.PROFILE.EXTERNAL_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<com.quizzes.api.common.model.enums.Lms> field3() {
		return com.quizzes.api.common.model.tables.Profile.PROFILE.LMS_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field4() {
		return com.quizzes.api.common.model.tables.Profile.PROFILE.PROFILE_DATA;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.sql.Timestamp> field5() {
		return com.quizzes.api.common.model.tables.Profile.PROFILE.CREATED_AT;
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
	public java.lang.String value2() {
		return getExternalId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public com.quizzes.api.common.model.enums.Lms value3() {
		return getLmsId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value4() {
		return getProfileData();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.sql.Timestamp value5() {
		return getCreatedAt();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProfileRecord value1(java.util.UUID value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProfileRecord value2(java.lang.String value) {
		setExternalId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProfileRecord value3(com.quizzes.api.common.model.enums.Lms value) {
		setLmsId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProfileRecord value4(java.lang.String value) {
		setProfileData(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProfileRecord value5(java.sql.Timestamp value) {
		setCreatedAt(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProfileRecord values(java.util.UUID value1, java.lang.String value2, com.quizzes.api.common.model.enums.Lms value3, java.lang.String value4, java.sql.Timestamp value5) {
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached ProfileRecord
	 */
	public ProfileRecord() {
		super(com.quizzes.api.common.model.tables.Profile.PROFILE);
	}

	/**
	 * Create a detached, initialised ProfileRecord
	 */
	public ProfileRecord(java.util.UUID id, java.lang.String externalId, com.quizzes.api.common.model.enums.Lms lmsId, java.lang.String profileData, java.sql.Timestamp createdAt) {
		super(com.quizzes.api.common.model.tables.Profile.PROFILE);

		setValue(0, id);
		setValue(1, externalId);
		setValue(2, lmsId);
		setValue(3, profileData);
		setValue(4, createdAt);
	}
}
