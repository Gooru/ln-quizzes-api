/**
 * This class is generated by jOOQ
 */
package com.quizzes.api.common.model.jooq.tables.records;

/**
 * This class is generated by jOOQ.
 */
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ContextProfileRecord extends org.jooq.impl.TableRecordImpl<com.quizzes.api.common.model.jooq.tables.records.ContextProfileRecord> implements org.jooq.Record7<java.util.UUID, java.util.UUID, java.util.UUID, java.util.UUID, java.sql.Timestamp, java.sql.Timestamp, java.lang.String> {

	private static final long serialVersionUID = 511026725;

	/**
	 * Setter for <code>public.context_profile.id</code>.
	 */
	public void setId(java.util.UUID value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>public.context_profile.id</code>.
	 */
	public java.util.UUID getId() {
		return (java.util.UUID) getValue(0);
	}

	/**
	 * Setter for <code>public.context_profile.context_id</code>.
	 */
	public void setContextId(java.util.UUID value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>public.context_profile.context_id</code>.
	 */
	public java.util.UUID getContextId() {
		return (java.util.UUID) getValue(1);
	}

	/**
	 * Setter for <code>public.context_profile.profile_id</code>.
	 */
	public void setProfileId(java.util.UUID value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>public.context_profile.profile_id</code>.
	 */
	public java.util.UUID getProfileId() {
		return (java.util.UUID) getValue(2);
	}

	/**
	 * Setter for <code>public.context_profile.current_resource_id</code>.
	 */
	public void setCurrentResourceId(java.util.UUID value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>public.context_profile.current_resource_id</code>.
	 */
	public java.util.UUID getCurrentResourceId() {
		return (java.util.UUID) getValue(3);
	}

	/**
	 * Setter for <code>public.context_profile.created_at</code>.
	 */
	public void setCreatedAt(java.sql.Timestamp value) {
		setValue(4, value);
	}

	/**
	 * Getter for <code>public.context_profile.created_at</code>.
	 */
	public java.sql.Timestamp getCreatedAt() {
		return (java.sql.Timestamp) getValue(4);
	}

	/**
	 * Setter for <code>public.context_profile.updated_at</code>.
	 */
	public void setUpdatedAt(java.sql.Timestamp value) {
		setValue(5, value);
	}

	/**
	 * Getter for <code>public.context_profile.updated_at</code>.
	 */
	public java.sql.Timestamp getUpdatedAt() {
		return (java.sql.Timestamp) getValue(5);
	}

	/**
	 * Setter for <code>public.context_profile.event_summary_data</code>.
	 */
	public void setEventSummaryData(java.lang.String value) {
		setValue(6, value);
	}

	/**
	 * Getter for <code>public.context_profile.event_summary_data</code>.
	 */
	public java.lang.String getEventSummaryData() {
		return (java.lang.String) getValue(6);
	}

	// -------------------------------------------------------------------------
	// Record7 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row7<java.util.UUID, java.util.UUID, java.util.UUID, java.util.UUID, java.sql.Timestamp, java.sql.Timestamp, java.lang.String> fieldsRow() {
		return (org.jooq.Row7) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row7<java.util.UUID, java.util.UUID, java.util.UUID, java.util.UUID, java.sql.Timestamp, java.sql.Timestamp, java.lang.String> valuesRow() {
		return (org.jooq.Row7) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.util.UUID> field1() {
		return com.quizzes.api.common.model.jooq.tables.ContextProfile.CONTEXT_PROFILE.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.util.UUID> field2() {
		return com.quizzes.api.common.model.jooq.tables.ContextProfile.CONTEXT_PROFILE.CONTEXT_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.util.UUID> field3() {
		return com.quizzes.api.common.model.jooq.tables.ContextProfile.CONTEXT_PROFILE.PROFILE_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.util.UUID> field4() {
		return com.quizzes.api.common.model.jooq.tables.ContextProfile.CONTEXT_PROFILE.CURRENT_RESOURCE_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.sql.Timestamp> field5() {
		return com.quizzes.api.common.model.jooq.tables.ContextProfile.CONTEXT_PROFILE.CREATED_AT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.sql.Timestamp> field6() {
		return com.quizzes.api.common.model.jooq.tables.ContextProfile.CONTEXT_PROFILE.UPDATED_AT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field7() {
		return com.quizzes.api.common.model.jooq.tables.ContextProfile.CONTEXT_PROFILE.EVENT_SUMMARY_DATA;
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
		return getContextId();
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
	public java.util.UUID value4() {
		return getCurrentResourceId();
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
	public java.sql.Timestamp value6() {
		return getUpdatedAt();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value7() {
		return getEventSummaryData();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContextProfileRecord value1(java.util.UUID value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContextProfileRecord value2(java.util.UUID value) {
		setContextId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContextProfileRecord value3(java.util.UUID value) {
		setProfileId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContextProfileRecord value4(java.util.UUID value) {
		setCurrentResourceId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContextProfileRecord value5(java.sql.Timestamp value) {
		setCreatedAt(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContextProfileRecord value6(java.sql.Timestamp value) {
		setUpdatedAt(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContextProfileRecord value7(java.lang.String value) {
		setEventSummaryData(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContextProfileRecord values(java.util.UUID value1, java.util.UUID value2, java.util.UUID value3, java.util.UUID value4, java.sql.Timestamp value5, java.sql.Timestamp value6, java.lang.String value7) {
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached ContextProfileRecord
	 */
	public ContextProfileRecord() {
		super(com.quizzes.api.common.model.jooq.tables.ContextProfile.CONTEXT_PROFILE);
	}

	/**
	 * Create a detached, initialised ContextProfileRecord
	 */
	public ContextProfileRecord(java.util.UUID id, java.util.UUID contextId, java.util.UUID profileId, java.util.UUID currentResourceId, java.sql.Timestamp createdAt, java.sql.Timestamp updatedAt, java.lang.String eventSummaryData) {
		super(com.quizzes.api.common.model.jooq.tables.ContextProfile.CONTEXT_PROFILE);

		setValue(0, id);
		setValue(1, contextId);
		setValue(2, profileId);
		setValue(3, currentResourceId);
		setValue(4, createdAt);
		setValue(5, updatedAt);
		setValue(6, eventSummaryData);
	}
}
