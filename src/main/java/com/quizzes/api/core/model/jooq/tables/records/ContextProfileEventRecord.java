/**
 * This class is generated by jOOQ
 */
package com.quizzes.api.core.model.jooq.tables.records;

/**
 * This class is generated by jOOQ.
 */
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ContextProfileEventRecord extends org.jooq.impl.TableRecordImpl<com.quizzes.api.core.model.jooq.tables.records.ContextProfileEventRecord> implements org.jooq.Record5<java.util.UUID, java.util.UUID, java.util.UUID, java.lang.String, java.sql.Timestamp> {

	private static final long serialVersionUID = 451466793;

	/**
	 * Setter for <code>public.context_profile_event.id</code>.
	 */
	public void setId(java.util.UUID value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>public.context_profile_event.id</code>.
	 */
	public java.util.UUID getId() {
		return (java.util.UUID) getValue(0);
	}

	/**
	 * Setter for <code>public.context_profile_event.context_profile_id</code>.
	 */
	public void setContextProfileId(java.util.UUID value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>public.context_profile_event.context_profile_id</code>.
	 */
	public java.util.UUID getContextProfileId() {
		return (java.util.UUID) getValue(1);
	}

	/**
	 * Setter for <code>public.context_profile_event.resource_id</code>.
	 */
	public void setResourceId(java.util.UUID value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>public.context_profile_event.resource_id</code>.
	 */
	public java.util.UUID getResourceId() {
		return (java.util.UUID) getValue(2);
	}

	/**
	 * Setter for <code>public.context_profile_event.event_data</code>.
	 */
	public void setEventData(java.lang.String value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>public.context_profile_event.event_data</code>.
	 */
	public java.lang.String getEventData() {
		return (java.lang.String) getValue(3);
	}

	/**
	 * Setter for <code>public.context_profile_event.created_at</code>.
	 */
	public void setCreatedAt(java.sql.Timestamp value) {
		setValue(4, value);
	}

	/**
	 * Getter for <code>public.context_profile_event.created_at</code>.
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
	public org.jooq.Row5<java.util.UUID, java.util.UUID, java.util.UUID, java.lang.String, java.sql.Timestamp> fieldsRow() {
		return (org.jooq.Row5) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row5<java.util.UUID, java.util.UUID, java.util.UUID, java.lang.String, java.sql.Timestamp> valuesRow() {
		return (org.jooq.Row5) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.util.UUID> field1() {
		return com.quizzes.api.core.model.jooq.tables.ContextProfileEvent.CONTEXT_PROFILE_EVENT.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.util.UUID> field2() {
		return com.quizzes.api.core.model.jooq.tables.ContextProfileEvent.CONTEXT_PROFILE_EVENT.CONTEXT_PROFILE_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.util.UUID> field3() {
		return com.quizzes.api.core.model.jooq.tables.ContextProfileEvent.CONTEXT_PROFILE_EVENT.RESOURCE_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field4() {
		return com.quizzes.api.core.model.jooq.tables.ContextProfileEvent.CONTEXT_PROFILE_EVENT.EVENT_DATA;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.sql.Timestamp> field5() {
		return com.quizzes.api.core.model.jooq.tables.ContextProfileEvent.CONTEXT_PROFILE_EVENT.CREATED_AT;
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
		return getContextProfileId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.UUID value3() {
		return getResourceId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value4() {
		return getEventData();
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
	public ContextProfileEventRecord value1(java.util.UUID value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContextProfileEventRecord value2(java.util.UUID value) {
		setContextProfileId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContextProfileEventRecord value3(java.util.UUID value) {
		setResourceId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContextProfileEventRecord value4(java.lang.String value) {
		setEventData(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContextProfileEventRecord value5(java.sql.Timestamp value) {
		setCreatedAt(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContextProfileEventRecord values(java.util.UUID value1, java.util.UUID value2, java.util.UUID value3, java.lang.String value4, java.sql.Timestamp value5) {
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached ContextProfileEventRecord
	 */
	public ContextProfileEventRecord() {
		super(com.quizzes.api.core.model.jooq.tables.ContextProfileEvent.CONTEXT_PROFILE_EVENT);
	}

	/**
	 * Create a detached, initialised ContextProfileEventRecord
	 */
	public ContextProfileEventRecord(java.util.UUID id, java.util.UUID contextProfileId, java.util.UUID resourceId, java.lang.String eventData, java.sql.Timestamp createdAt) {
		super(com.quizzes.api.core.model.jooq.tables.ContextProfileEvent.CONTEXT_PROFILE_EVENT);

		setValue(0, id);
		setValue(1, contextProfileId);
		setValue(2, resourceId);
		setValue(3, eventData);
		setValue(4, createdAt);
	}
}
