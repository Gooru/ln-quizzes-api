/**
 * This class is generated by jOOQ
 */
package com.quizzes.api.common.model.jooq.tables;

/**
 * This class is generated by jOOQ.
 */
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Collection extends org.jooq.impl.TableImpl<com.quizzes.api.common.model.jooq.tables.records.CollectionRecord> {

	private static final long serialVersionUID = 174209963;

	/**
	 * The singleton instance of <code>public.collection</code>
	 */
	public static final com.quizzes.api.common.model.jooq.tables.Collection COLLECTION = new com.quizzes.api.common.model.jooq.tables.Collection();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<com.quizzes.api.common.model.jooq.tables.records.CollectionRecord> getRecordType() {
		return com.quizzes.api.common.model.jooq.tables.records.CollectionRecord.class;
	}

	/**
	 * The column <code>public.collection.id</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.common.model.jooq.tables.records.CollectionRecord, java.util.UUID> ID = createField("id", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

	/**
	 * The column <code>public.collection.external_id</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.common.model.jooq.tables.records.CollectionRecord, java.lang.String> EXTERNAL_ID = createField("external_id", org.jooq.impl.SQLDataType.VARCHAR.length(50).nullable(false), this, "");

	/**
	 * The column <code>public.collection.lms_id</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.common.model.jooq.tables.records.CollectionRecord, com.quizzes.api.common.model.jooq.enums.Lms> LMS_ID = createField("lms_id", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(com.quizzes.api.common.model.jooq.enums.Lms.class), this, "");

	/**
	 * The column <code>public.collection.is_collection</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.common.model.jooq.tables.records.CollectionRecord, java.lang.Boolean> IS_COLLECTION = createField("is_collection", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>public.collection.owner_profile_id</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.common.model.jooq.tables.records.CollectionRecord, java.util.UUID> OWNER_PROFILE_ID = createField("owner_profile_id", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

	/**
	 * The column <code>public.collection.collection_data</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.common.model.jooq.tables.records.CollectionRecord, java.lang.String> COLLECTION_DATA = createField("collection_data", org.jooq.impl.DefaultDataType.getDefaultDataType("jsonb"), this, "", new com.quizzes.api.common.binding.PostgresJsonbStringBinding());

	/**
	 * The column <code>public.collection.is_locked</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.common.model.jooq.tables.records.CollectionRecord, java.lang.Boolean> IS_LOCKED = createField("is_locked", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>public.collection.is_deleted</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.common.model.jooq.tables.records.CollectionRecord, java.lang.Boolean> IS_DELETED = createField("is_deleted", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>public.collection.created_at</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.common.model.jooq.tables.records.CollectionRecord, java.sql.Timestamp> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>public.collection.updated_at</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.common.model.jooq.tables.records.CollectionRecord, java.sql.Timestamp> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaulted(true), this, "");

	/**
	 * Create a <code>public.collection</code> table reference
	 */
	public Collection() {
		this("collection", null);
	}

	/**
	 * Create an aliased <code>public.collection</code> table reference
	 */
	public Collection(java.lang.String alias) {
		this(alias, com.quizzes.api.common.model.jooq.tables.Collection.COLLECTION);
	}

	private Collection(java.lang.String alias, org.jooq.Table<com.quizzes.api.common.model.jooq.tables.records.CollectionRecord> aliased) {
		this(alias, aliased, null);
	}

	private Collection(java.lang.String alias, org.jooq.Table<com.quizzes.api.common.model.jooq.tables.records.CollectionRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, com.quizzes.api.common.model.jooq.Public.PUBLIC, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public com.quizzes.api.common.model.jooq.tables.Collection as(java.lang.String alias) {
		return new com.quizzes.api.common.model.jooq.tables.Collection(alias, this);
	}

	/**
	 * Rename this table
	 */
	public com.quizzes.api.common.model.jooq.tables.Collection rename(java.lang.String name) {
		return new com.quizzes.api.common.model.jooq.tables.Collection(name, null);
	}
}
