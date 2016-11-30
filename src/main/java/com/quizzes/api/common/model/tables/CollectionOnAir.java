/**
 * This class is generated by jOOQ
 */
package com.quizzes.api.common.model.tables;

/**
 * This class is generated by jOOQ.
 */
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CollectionOnAir extends org.jooq.impl.TableImpl<com.quizzes.api.common.model.tables.records.CollectionOnAirRecord> {

	private static final long serialVersionUID = 1977148548;

	/**
	 * The singleton instance of <code>public.collection_on_air</code>
	 */
	public static final com.quizzes.api.common.model.tables.CollectionOnAir COLLECTION_ON_AIR = new com.quizzes.api.common.model.tables.CollectionOnAir();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<com.quizzes.api.common.model.tables.records.CollectionOnAirRecord> getRecordType() {
		return com.quizzes.api.common.model.tables.records.CollectionOnAirRecord.class;
	}

	/**
	 * The column <code>public.collection_on_air.id</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.common.model.tables.records.CollectionOnAirRecord, java.util.UUID> ID = createField("id", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

	/**
	 * The column <code>public.collection_on_air.class_id</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.common.model.tables.records.CollectionOnAirRecord, java.lang.String> CLASS_ID = createField("class_id", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>public.collection_on_air.collection_id</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.common.model.tables.records.CollectionOnAirRecord, java.lang.String> COLLECTION_ID = createField("collection_id", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * Create a <code>public.collection_on_air</code> table reference
	 */
	public CollectionOnAir() {
		this("collection_on_air", null);
	}

	/**
	 * Create an aliased <code>public.collection_on_air</code> table reference
	 */
	public CollectionOnAir(java.lang.String alias) {
		this(alias, com.quizzes.api.common.model.tables.CollectionOnAir.COLLECTION_ON_AIR);
	}

	private CollectionOnAir(java.lang.String alias, org.jooq.Table<com.quizzes.api.common.model.tables.records.CollectionOnAirRecord> aliased) {
		this(alias, aliased, null);
	}

	private CollectionOnAir(java.lang.String alias, org.jooq.Table<com.quizzes.api.common.model.tables.records.CollectionOnAirRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, com.quizzes.api.common.model.Public.PUBLIC, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public com.quizzes.api.common.model.tables.CollectionOnAir as(java.lang.String alias) {
		return new com.quizzes.api.common.model.tables.CollectionOnAir(alias, this);
	}

	/**
	 * Rename this table
	 */
	public com.quizzes.api.common.model.tables.CollectionOnAir rename(java.lang.String name) {
		return new com.quizzes.api.common.model.tables.CollectionOnAir(name, null);
	}
}
