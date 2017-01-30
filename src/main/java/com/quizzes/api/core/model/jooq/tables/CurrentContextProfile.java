/**
 * This class is generated by jOOQ
 */
package com.quizzes.api.core.model.jooq.tables;

/**
 * This class is generated by jOOQ.
 */
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CurrentContextProfile extends org.jooq.impl.TableImpl<com.quizzes.api.core.model.jooq.tables.records.CurrentContextProfileRecord> {

	private static final long serialVersionUID = -1804199629;

	/**
	 * The singleton instance of <code>public.current_context_profile</code>
	 */
	public static final com.quizzes.api.core.model.jooq.tables.CurrentContextProfile CURRENT_CONTEXT_PROFILE = new com.quizzes.api.core.model.jooq.tables.CurrentContextProfile();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<com.quizzes.api.core.model.jooq.tables.records.CurrentContextProfileRecord> getRecordType() {
		return com.quizzes.api.core.model.jooq.tables.records.CurrentContextProfileRecord.class;
	}

	/**
	 * The column <code>public.current_context_profile.context_id</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.core.model.jooq.tables.records.CurrentContextProfileRecord, java.util.UUID> CONTEXT_ID = createField("context_id", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

	/**
	 * The column <code>public.current_context_profile.profile_id</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.core.model.jooq.tables.records.CurrentContextProfileRecord, java.util.UUID> PROFILE_ID = createField("profile_id", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

	/**
	 * The column <code>public.current_context_profile.context_profile_id</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.core.model.jooq.tables.records.CurrentContextProfileRecord, java.util.UUID> CONTEXT_PROFILE_ID = createField("context_profile_id", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

	/**
	 * The column <code>public.current_context_profile.created_at</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.core.model.jooq.tables.records.CurrentContextProfileRecord, java.sql.Timestamp> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaulted(true), this, "");

	/**
	 * Create a <code>public.current_context_profile</code> table reference
	 */
	public CurrentContextProfile() {
		this("current_context_profile", null);
	}

	/**
	 * Create an aliased <code>public.current_context_profile</code> table reference
	 */
	public CurrentContextProfile(java.lang.String alias) {
		this(alias, com.quizzes.api.core.model.jooq.tables.CurrentContextProfile.CURRENT_CONTEXT_PROFILE);
	}

	private CurrentContextProfile(java.lang.String alias, org.jooq.Table<com.quizzes.api.core.model.jooq.tables.records.CurrentContextProfileRecord> aliased) {
		this(alias, aliased, null);
	}

	private CurrentContextProfile(java.lang.String alias, org.jooq.Table<com.quizzes.api.core.model.jooq.tables.records.CurrentContextProfileRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, com.quizzes.api.core.model.jooq.Public.PUBLIC, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public com.quizzes.api.core.model.jooq.tables.CurrentContextProfile as(java.lang.String alias) {
		return new com.quizzes.api.core.model.jooq.tables.CurrentContextProfile(alias, this);
	}

	/**
	 * Rename this table
	 */
	public com.quizzes.api.core.model.jooq.tables.CurrentContextProfile rename(java.lang.String name) {
		return new com.quizzes.api.core.model.jooq.tables.CurrentContextProfile(name, null);
	}
}
