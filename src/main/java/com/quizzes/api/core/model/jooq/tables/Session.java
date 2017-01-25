/**
 * This class is generated by jOOQ
 */
package com.quizzes.api.core.model.jooq.tables;

/**
 * This class is generated by jOOQ.
 */
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Session extends org.jooq.impl.TableImpl<com.quizzes.api.core.model.jooq.tables.records.SessionRecord> {

	private static final long serialVersionUID = -663994401;

	/**
	 * The singleton instance of <code>public.session</code>
	 */
	public static final com.quizzes.api.core.model.jooq.tables.Session SESSION = new com.quizzes.api.core.model.jooq.tables.Session();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<com.quizzes.api.core.model.jooq.tables.records.SessionRecord> getRecordType() {
		return com.quizzes.api.core.model.jooq.tables.records.SessionRecord.class;
	}

	/**
	 * The column <code>public.session.id</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.core.model.jooq.tables.records.SessionRecord, java.util.UUID> ID = createField("id", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

	/**
	 * The column <code>public.session.profile_id</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.core.model.jooq.tables.records.SessionRecord, java.util.UUID> PROFILE_ID = createField("profile_id", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

	/**
	 * The column <code>public.session.created_at</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.core.model.jooq.tables.records.SessionRecord, java.sql.Timestamp> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>public.session.last_access_at</code>.
	 */
	public final org.jooq.TableField<com.quizzes.api.core.model.jooq.tables.records.SessionRecord, java.sql.Timestamp> LAST_ACCESS_AT = createField("last_access_at", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaulted(true), this, "");

	/**
	 * Create a <code>public.session</code> table reference
	 */
	public Session() {
		this("session", null);
	}

	/**
	 * Create an aliased <code>public.session</code> table reference
	 */
	public Session(java.lang.String alias) {
		this(alias, com.quizzes.api.core.model.jooq.tables.Session.SESSION);
	}

	private Session(java.lang.String alias, org.jooq.Table<com.quizzes.api.core.model.jooq.tables.records.SessionRecord> aliased) {
		this(alias, aliased, null);
	}

	private Session(java.lang.String alias, org.jooq.Table<com.quizzes.api.core.model.jooq.tables.records.SessionRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, com.quizzes.api.core.model.jooq.Public.PUBLIC, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public com.quizzes.api.core.model.jooq.tables.Session as(java.lang.String alias) {
		return new com.quizzes.api.core.model.jooq.tables.Session(alias, this);
	}

	/**
	 * Rename this table
	 */
	public com.quizzes.api.core.model.jooq.tables.Session rename(java.lang.String name) {
		return new com.quizzes.api.core.model.jooq.tables.Session(name, null);
	}
}
