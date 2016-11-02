/**
 * This class is generated by jOOQ
 */
package com.quizzes.api.common.model.tables;

import com.quizzes.api.common.model.Public;

/**
 * This class is generated by jOOQ.
 */
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Resource extends org.jooq.impl.TableImpl<com.quizzes.api.common.model.tables.records.ResourceRecord> {

	private static final long serialVersionUID = -1900493424;

	/**
	 * The singleton instance of <code>public.resource</code>
	 */
	public static final com.quizzes.api.common.model.tables.Resource RESOURCE = new com.quizzes.api.common.model.tables.Resource();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<com.quizzes.api.common.model.tables.records.ResourceRecord> getRecordType() {
		return com.quizzes.api.common.model.tables.records.ResourceRecord.class;
	}

	/**
	 * The column <code>public.resource.id</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.common.model.tables.records.ResourceRecord, java.util.UUID> ID = createField("id", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

	/**
	 * The column <code>public.resource.external_id</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.common.model.tables.records.ResourceRecord, java.lang.String> EXTERNAL_ID = createField("external_id", org.jooq.impl.SQLDataType.VARCHAR.length(50).nullable(false), this, "");

	/**
	 * The column <code>public.resource.lms_id</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.common.model.tables.records.ResourceRecord, com.quizzes.api.common.model.enums.Lms> LMS_ID = createField("lms_id", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(com.quizzes.api.common.model.enums.Lms.class), this, "");

	/**
	 * The column <code>public.resource.collection_id</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.common.model.tables.records.ResourceRecord, java.util.UUID> COLLECTION_ID = createField("collection_id", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

	/**
	 * The column <code>public.resource.is_resource</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.common.model.tables.records.ResourceRecord, java.lang.Boolean> IS_RESOURCE = createField("is_resource", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>public.resource.owner_profile_id</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.common.model.tables.records.ResourceRecord, java.util.UUID> OWNER_PROFILE_ID = createField("owner_profile_id", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

	/**
	 * The column <code>public.resource.resource_data</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.common.model.tables.records.ResourceRecord, java.lang.String> RESOURCE_DATA = createField("resource_data", org.jooq.impl.DefaultDataType.getDefaultDataType("jsonb"), this, "", new com.quizzes.api.common.model.binding.PostgresJsonbStringBinding());

	/**
	 * The column <code>public.resource.sequence</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.common.model.tables.records.ResourceRecord, java.lang.Short> SEQUENCE = createField("sequence", org.jooq.impl.SQLDataType.SMALLINT.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>public.resource.is_deleted</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.common.model.tables.records.ResourceRecord, java.lang.Boolean> IS_DELETED = createField("is_deleted", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>public.resource.created_at</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.common.model.tables.records.ResourceRecord, java.sql.Timestamp> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaulted(true), this, "");

	/**
	 * Create a <code>public.resource</code> table reference
	 */
	public Resource() {
		this("resource", null);
	}

	/**
	 * Create an aliased <code>public.resource</code> table reference
	 */
	public Resource(java.lang.String alias) {
		this(alias, com.quizzes.api.common.model.tables.Resource.RESOURCE);
	}

	private Resource(java.lang.String alias, org.jooq.Table<com.quizzes.api.common.model.tables.records.ResourceRecord> aliased) {
		this(alias, aliased, null);
	}

	private Resource(java.lang.String alias, org.jooq.Table<com.quizzes.api.common.model.tables.records.ResourceRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, Public.PUBLIC, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public com.quizzes.api.common.model.tables.Resource as(java.lang.String alias) {
		return new com.quizzes.api.common.model.tables.Resource(alias, this);
	}

	/**
	 * Rename this table
	 */
	public com.quizzes.api.common.model.tables.Resource rename(java.lang.String name) {
		return new com.quizzes.api.common.model.tables.Resource(name, null);
	}
}