/**
 * This class is generated by jOOQ
 */
package com.quizzes.api.common.model.jooq.tables.records;

/**
 * This class is generated by jOOQ.
 */
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ClientRecord extends org.jooq.impl.TableRecordImpl<com.quizzes.api.common.model.jooq.tables.records.ClientRecord> implements org.jooq.Record8<java.util.UUID, java.lang.String, java.lang.String, java.lang.Boolean, java.util.UUID, byte[], java.sql.Timestamp, java.sql.Timestamp> {

	private static final long serialVersionUID = -2116661932;

	/**
	 * Setter for <code>public.client.id</code>.
	 */
	public void setId(java.util.UUID value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>public.client.id</code>.
	 */
	public java.util.UUID getId() {
		return (java.util.UUID) getValue(0);
	}

	/**
	 * Setter for <code>public.client.name</code>.
	 */
	public void setName(java.lang.String value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>public.client.name</code>.
	 */
	public java.lang.String getName() {
		return (java.lang.String) getValue(1);
	}

	/**
	 * Setter for <code>public.client.description</code>.
	 */
	public void setDescription(java.lang.String value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>public.client.description</code>.
	 */
	public java.lang.String getDescription() {
		return (java.lang.String) getValue(2);
	}

	/**
	 * Setter for <code>public.client.is_active</code>.
	 */
	public void setIsActive(java.lang.Boolean value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>public.client.is_active</code>.
	 */
	public java.lang.Boolean getIsActive() {
		return (java.lang.Boolean) getValue(3);
	}

	/**
	 * Setter for <code>public.client.api_key</code>.
	 */
	public void setApiKey(java.util.UUID value) {
		setValue(4, value);
	}

	/**
	 * Getter for <code>public.client.api_key</code>.
	 */
	public java.util.UUID getApiKey() {
		return (java.util.UUID) getValue(4);
	}

	/**
	 * Setter for <code>public.client.api_secret</code>.
	 */
	public void setApiSecret(byte[] value) {
		setValue(5, value);
	}

	/**
	 * Getter for <code>public.client.api_secret</code>.
	 */
	public byte[] getApiSecret() {
		return (byte[]) getValue(5);
	}

	/**
	 * Setter for <code>public.client.created_at</code>.
	 */
	public void setCreatedAt(java.sql.Timestamp value) {
		setValue(6, value);
	}

	/**
	 * Getter for <code>public.client.created_at</code>.
	 */
	public java.sql.Timestamp getCreatedAt() {
		return (java.sql.Timestamp) getValue(6);
	}

	/**
	 * Setter for <code>public.client.updated_at</code>.
	 */
	public void setUpdatedAt(java.sql.Timestamp value) {
		setValue(7, value);
	}

	/**
	 * Getter for <code>public.client.updated_at</code>.
	 */
	public java.sql.Timestamp getUpdatedAt() {
		return (java.sql.Timestamp) getValue(7);
	}

	// -------------------------------------------------------------------------
	// Record8 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row8<java.util.UUID, java.lang.String, java.lang.String, java.lang.Boolean, java.util.UUID, byte[], java.sql.Timestamp, java.sql.Timestamp> fieldsRow() {
		return (org.jooq.Row8) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row8<java.util.UUID, java.lang.String, java.lang.String, java.lang.Boolean, java.util.UUID, byte[], java.sql.Timestamp, java.sql.Timestamp> valuesRow() {
		return (org.jooq.Row8) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.util.UUID> field1() {
		return com.quizzes.api.common.model.jooq.tables.Client.CLIENT.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field2() {
		return com.quizzes.api.common.model.jooq.tables.Client.CLIENT.NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field3() {
		return com.quizzes.api.common.model.jooq.tables.Client.CLIENT.DESCRIPTION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Boolean> field4() {
		return com.quizzes.api.common.model.jooq.tables.Client.CLIENT.IS_ACTIVE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.util.UUID> field5() {
		return com.quizzes.api.common.model.jooq.tables.Client.CLIENT.API_KEY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<byte[]> field6() {
		return com.quizzes.api.common.model.jooq.tables.Client.CLIENT.API_SECRET;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.sql.Timestamp> field7() {
		return com.quizzes.api.common.model.jooq.tables.Client.CLIENT.CREATED_AT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.sql.Timestamp> field8() {
		return com.quizzes.api.common.model.jooq.tables.Client.CLIENT.UPDATED_AT;
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
		return getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value3() {
		return getDescription();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Boolean value4() {
		return getIsActive();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.UUID value5() {
		return getApiKey();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] value6() {
		return getApiSecret();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.sql.Timestamp value7() {
		return getCreatedAt();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.sql.Timestamp value8() {
		return getUpdatedAt();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClientRecord value1(java.util.UUID value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClientRecord value2(java.lang.String value) {
		setName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClientRecord value3(java.lang.String value) {
		setDescription(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClientRecord value4(java.lang.Boolean value) {
		setIsActive(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClientRecord value5(java.util.UUID value) {
		setApiKey(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClientRecord value6(byte[] value) {
		setApiSecret(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClientRecord value7(java.sql.Timestamp value) {
		setCreatedAt(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClientRecord value8(java.sql.Timestamp value) {
		setUpdatedAt(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClientRecord values(java.util.UUID value1, java.lang.String value2, java.lang.String value3, java.lang.Boolean value4, java.util.UUID value5, byte[] value6, java.sql.Timestamp value7, java.sql.Timestamp value8) {
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached ClientRecord
	 */
	public ClientRecord() {
		super(com.quizzes.api.common.model.jooq.tables.Client.CLIENT);
	}

	/**
	 * Create a detached, initialised ClientRecord
	 */
	public ClientRecord(java.util.UUID id, java.lang.String name, java.lang.String description, java.lang.Boolean isActive, java.util.UUID apiKey, byte[] apiSecret, java.sql.Timestamp createdAt, java.sql.Timestamp updatedAt) {
		super(com.quizzes.api.common.model.jooq.tables.Client.CLIENT);

		setValue(0, id);
		setValue(1, name);
		setValue(2, description);
		setValue(3, isActive);
		setValue(4, apiKey);
		setValue(5, apiSecret);
		setValue(6, createdAt);
		setValue(7, updatedAt);
	}
}
