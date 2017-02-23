/**
 * This class is generated by jOOQ
 */
package com.quizzes.api.core.model.jooq.tables;

/**
 * This class is generated by jOOQ.
 */
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ContextProfile extends org.jooq.impl.TableImpl<com.quizzes.api.core.model.jooq.tables.records.ContextProfileRecord> {

	private static final long serialVersionUID = 108758228;

	/**
	 * The singleton instance of <code>public.context_profile</code>
	 */
	public static final com.quizzes.api.core.model.jooq.tables.ContextProfile CONTEXT_PROFILE = new com.quizzes.api.core.model.jooq.tables.ContextProfile();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<com.quizzes.api.core.model.jooq.tables.records.ContextProfileRecord> getRecordType() {
		return com.quizzes.api.core.model.jooq.tables.records.ContextProfileRecord.class;
	}

	/**
	 * The column <code>public.context_profile.id</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.core.model.jooq.tables.records.ContextProfileRecord, java.util.UUID> ID = createField("id", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

	/**
	 * The column <code>public.context_profile.context_id</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.core.model.jooq.tables.records.ContextProfileRecord, java.util.UUID> CONTEXT_ID = createField("context_id", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

	/**
	 * The column <code>public.context_profile.profile_id</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.core.model.jooq.tables.records.ContextProfileRecord, java.util.UUID> PROFILE_ID = createField("profile_id", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

	/**
	 * The column <code>public.context_profile.current_resource_id</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.core.model.jooq.tables.records.ContextProfileRecord, java.util.UUID> CURRENT_RESOURCE_ID = createField("current_resource_id", org.jooq.impl.SQLDataType.UUID, this, "");

	/**
	 * The column <code>public.context_profile.is_complete</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.core.model.jooq.tables.records.ContextProfileRecord, java.lang.Boolean> IS_COMPLETE = createField("is_complete", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>public.context_profile.event_summary_data</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.core.model.jooq.tables.records.ContextProfileRecord, java.lang.String> EVENT_SUMMARY_DATA = createField("event_summary_data", org.jooq.impl.DefaultDataType.getDefaultDataType("jsonb"), this, "", new com.quizzes.api.core.model.binding.PostgresJsonbStringBinding());

	/**
	 * The column <code>public.context_profile.taxonomy_sumary_data</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.core.model.jooq.tables.records.ContextProfileRecord, java.lang.String> TAXONOMY_SUMARY_DATA = createField("taxonomy_sumary_data", org.jooq.impl.DefaultDataType.getDefaultDataType("jsonb"), this, "", new com.quizzes.api.core.model.binding.PostgresJsonbStringBinding());

	/**
	 * The column <code>public.context_profile.created_at</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.core.model.jooq.tables.records.ContextProfileRecord, java.sql.Timestamp> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>public.context_profile.updated_at</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.core.model.jooq.tables.records.ContextProfileRecord, java.sql.Timestamp> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaulted(true), this, "");

	/**
	 * Create a <code>public.context_profile</code> table reference
	 */
	public ContextProfile() {
		this("context_profile", null);
	}

	/**
	 * Create an aliased <code>public.context_profile</code> table reference
	 */
	public ContextProfile(java.lang.String alias) {
		this(alias, com.quizzes.api.core.model.jooq.tables.ContextProfile.CONTEXT_PROFILE);
	}

	private ContextProfile(java.lang.String alias, org.jooq.Table<com.quizzes.api.core.model.jooq.tables.records.ContextProfileRecord> aliased) {
		this(alias, aliased, null);
	}

	private ContextProfile(java.lang.String alias, org.jooq.Table<com.quizzes.api.core.model.jooq.tables.records.ContextProfileRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, com.quizzes.api.core.model.jooq.Public.PUBLIC, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public com.quizzes.api.core.model.jooq.tables.ContextProfile as(java.lang.String alias) {
		return new com.quizzes.api.core.model.jooq.tables.ContextProfile(alias, this);
	}

	/**
	 * Rename this table
	 */
	public com.quizzes.api.core.model.jooq.tables.ContextProfile rename(java.lang.String name) {
		return new com.quizzes.api.core.model.jooq.tables.ContextProfile(name, null);
	}
}
