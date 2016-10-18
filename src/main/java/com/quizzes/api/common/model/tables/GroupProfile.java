/**
 * This class is generated by jOOQ
 */
package com.quizzes.api.common.model.tables;

import com.quizzes.api.common.model.Public;

/**
 * This class is generated by jOOQ.
 */
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class GroupProfile extends org.jooq.impl.TableImpl<com.quizzes.api.common.model.tables.records.GroupProfileRecord> {

	private static final long serialVersionUID = -988589349;

	/**
	 * The singleton instance of <code>public.group_profile</code>
	 */
	public static final com.quizzes.api.common.model.tables.GroupProfile GROUP_PROFILE = new com.quizzes.api.common.model.tables.GroupProfile();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<com.quizzes.api.common.model.tables.records.GroupProfileRecord> getRecordType() {
		return com.quizzes.api.common.model.tables.records.GroupProfileRecord.class;
	}

	/**
	 * The column <code>public.group_profile.id</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.common.model.tables.records.GroupProfileRecord, java.util.UUID> ID = createField("id", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

	/**
	 * The column <code>public.group_profile.group_id</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.common.model.tables.records.GroupProfileRecord, java.util.UUID> GROUP_ID = createField("group_id", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

	/**
	 * The column <code>public.group_profile.profile_id</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.common.model.tables.records.GroupProfileRecord, java.util.UUID> PROFILE_ID = createField("profile_id", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

	/**
	 * The column <code>public.group_profile.created_at</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.common.model.tables.records.GroupProfileRecord, java.sql.Timestamp> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.TIMESTAMP.defaulted(true), this, "");

	/**
	 * Create a <code>public.group_profile</code> table reference
	 */
	public GroupProfile() {
		this("group_profile", null);
	}

	/**
	 * Create an aliased <code>public.group_profile</code> table reference
	 */
	public GroupProfile(java.lang.String alias) {
		this(alias, com.quizzes.api.common.model.tables.GroupProfile.GROUP_PROFILE);
	}

	private GroupProfile(java.lang.String alias, org.jooq.Table<com.quizzes.api.common.model.tables.records.GroupProfileRecord> aliased) {
		this(alias, aliased, null);
	}

	private GroupProfile(java.lang.String alias, org.jooq.Table<com.quizzes.api.common.model.tables.records.GroupProfileRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, Public.PUBLIC, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public com.quizzes.api.common.model.tables.GroupProfile as(java.lang.String alias) {
		return new com.quizzes.api.common.model.tables.GroupProfile(alias, this);
	}

	/**
	 * Rename this table
	 */
	public com.quizzes.api.common.model.tables.GroupProfile rename(java.lang.String name) {
		return new com.quizzes.api.common.model.tables.GroupProfile(name, null);
	}
}
