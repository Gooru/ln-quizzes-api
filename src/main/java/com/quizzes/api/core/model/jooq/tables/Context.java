/**
 * This class is generated by jOOQ
 */
package com.quizzes.api.core.model.jooq.tables;

/**
 * This class is generated by jOOQ.
 */
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Context extends org.jooq.impl.TableImpl<com.quizzes.api.core.model.jooq.tables.records.ContextRecord> {

	private static final long serialVersionUID = 1741921261;

	/**
	 * The singleton instance of <code>public.context</code>
	 */
	public static final com.quizzes.api.core.model.jooq.tables.Context CONTEXT = new com.quizzes.api.core.model.jooq.tables.Context();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<com.quizzes.api.core.model.jooq.tables.records.ContextRecord> getRecordType() {
		return com.quizzes.api.core.model.jooq.tables.records.ContextRecord.class;
	}

	/**
	 * The column <code>public.context.id</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.core.model.jooq.tables.records.ContextRecord, java.util.UUID> ID = createField("id", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

	/**
	 * The column <code>public.context.collection_id</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.core.model.jooq.tables.records.ContextRecord, java.util.UUID> COLLECTION_ID = createField("collection_id", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

	/**
	 * The column <code>public.context.profile_id</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.core.model.jooq.tables.records.ContextRecord, java.util.UUID> PROFILE_ID = createField("profile_id", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

	/**
	 * The column <code>public.context.class_id</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.core.model.jooq.tables.records.ContextRecord, java.util.UUID> CLASS_ID = createField("class_id", org.jooq.impl.SQLDataType.UUID, this, "");

	/**
	 * The column <code>public.context.context_data</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.core.model.jooq.tables.records.ContextRecord, java.lang.String> CONTEXT_DATA = createField("context_data", org.jooq.impl.DefaultDataType.getDefaultDataType("jsonb"), this, "", new com.quizzes.api.core.model.binding.PostgresJsonbStringBinding());

	/**
	 * The column <code>public.context.is_active</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.core.model.jooq.tables.records.ContextRecord, java.lang.Boolean> IS_ACTIVE = createField("is_active", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>public.context.start_date</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.core.model.jooq.tables.records.ContextRecord, java.sql.Timestamp> START_DATE = createField("start_date", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

	/**
	 * The column <code>public.context.due_date</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.core.model.jooq.tables.records.ContextRecord, java.sql.Timestamp> DUE_DATE = createField("due_date", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

	/**
	 * The column <code>public.context.is_deleted</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.core.model.jooq.tables.records.ContextRecord, java.lang.Boolean> IS_DELETED = createField("is_deleted", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>public.context.created_at</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.core.model.jooq.tables.records.ContextRecord, java.sql.Timestamp> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>public.context.updated_at</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.core.model.jooq.tables.records.ContextRecord, java.sql.Timestamp> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaulted(true), this, "");

	/**
	 * Create a <code>public.context</code> table reference
	 */
	public Context() {
		this("context", null);
	}

	/**
	 * Create an aliased <code>public.context</code> table reference
	 */
	public Context(java.lang.String alias) {
		this(alias, com.quizzes.api.core.model.jooq.tables.Context.CONTEXT);
	}

	private Context(java.lang.String alias, org.jooq.Table<com.quizzes.api.core.model.jooq.tables.records.ContextRecord> aliased) {
		this(alias, aliased, null);
	}

	private Context(java.lang.String alias, org.jooq.Table<com.quizzes.api.core.model.jooq.tables.records.ContextRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, com.quizzes.api.core.model.jooq.Public.PUBLIC, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public com.quizzes.api.core.model.jooq.tables.Context as(java.lang.String alias) {
		return new com.quizzes.api.core.model.jooq.tables.Context(alias, this);
	}

	/**
	 * Rename this table
	 */
	public com.quizzes.api.core.model.jooq.tables.Context rename(java.lang.String name) {
		return new com.quizzes.api.core.model.jooq.tables.Context(name, null);
	}
}
