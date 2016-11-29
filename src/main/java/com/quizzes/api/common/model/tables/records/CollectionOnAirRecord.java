/**
 * This class is generated by jOOQ
 */
package com.quizzes.api.common.model.tables.records;

/**
 * This class is generated by jOOQ.
 */
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CollectionOnAirRecord extends org.jooq.impl.TableRecordImpl<com.quizzes.api.common.model.tables.records.CollectionOnAirRecord> implements org.jooq.Record3<java.util.UUID, java.lang.String, java.lang.String> {

	private static final long serialVersionUID = -1345877386;

	/**
	 * Setter for <code>public.collection_on_air.id</code>.
	 */
	public void setId(java.util.UUID value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>public.collection_on_air.id</code>.
	 */
	public java.util.UUID getId() {
		return (java.util.UUID) getValue(0);
	}

	/**
	 * Setter for <code>public.collection_on_air.class_id</code>.
	 */
	public void setClassId(java.lang.String value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>public.collection_on_air.class_id</code>.
	 */
	public java.lang.String getClassId() {
		return (java.lang.String) getValue(1);
	}

	/**
	 * Setter for <code>public.collection_on_air.collection_id</code>.
	 */
	public void setCollectionId(java.lang.String value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>public.collection_on_air.collection_id</code>.
	 */
	public java.lang.String getCollectionId() {
		return (java.lang.String) getValue(2);
	}

	// -------------------------------------------------------------------------
	// Record3 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row3<java.util.UUID, java.lang.String, java.lang.String> fieldsRow() {
		return (org.jooq.Row3) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row3<java.util.UUID, java.lang.String, java.lang.String> valuesRow() {
		return (org.jooq.Row3) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.util.UUID> field1() {
		return com.quizzes.api.common.model.tables.CollectionOnAir.COLLECTION_ON_AIR.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field2() {
		return com.quizzes.api.common.model.tables.CollectionOnAir.COLLECTION_ON_AIR.CLASS_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field3() {
		return com.quizzes.api.common.model.tables.CollectionOnAir.COLLECTION_ON_AIR.COLLECTION_ID;
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
		return getClassId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value3() {
		return getCollectionId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CollectionOnAirRecord value1(java.util.UUID value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CollectionOnAirRecord value2(java.lang.String value) {
		setClassId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CollectionOnAirRecord value3(java.lang.String value) {
		setCollectionId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CollectionOnAirRecord values(java.util.UUID value1, java.lang.String value2, java.lang.String value3) {
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached CollectionOnAirRecord
	 */
	public CollectionOnAirRecord() {
		super(com.quizzes.api.common.model.tables.CollectionOnAir.COLLECTION_ON_AIR);
	}

	/**
	 * Create a detached, initialised CollectionOnAirRecord
	 */
	public CollectionOnAirRecord(java.util.UUID id, java.lang.String classId, java.lang.String collectionId) {
		super(com.quizzes.api.common.model.tables.CollectionOnAir.COLLECTION_ON_AIR);

		setValue(0, id);
		setValue(1, classId);
		setValue(2, collectionId);
	}
}
