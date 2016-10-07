/**
 * This class is generated by jOOQ
 */
package com.quizzes.api.common.tables;

/**
 * This class is generated by jOOQ.
 */
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Event extends org.jooq.impl.TableImpl<org.jooq.Record> {

	private static final long serialVersionUID = 1165564104;

	/**
	 * The singleton instance of <code>public.event</code>
	 */
	public static final com.quizzes.api.common.tables.Event EVENT = new com.quizzes.api.common.tables.Event();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<org.jooq.Record> getRecordType() {
		return org.jooq.Record.class;
	}

	/**
	 * The column <code>public.event.id</code>.
	 */
	public final org.jooq.TableField<org.jooq.Record, java.util.UUID> ID = createField("id", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

	/**
	 * The column <code>public.event.event_index</code>.
	 */
	public final org.jooq.TableField<org.jooq.Record, java.util.UUID> EVENT_INDEX = createField("event_index", org.jooq.impl.SQLDataType.UUID, this, "");

	/**
	 * The column <code>public.event.created_at</code>.
	 */
	public final org.jooq.TableField<org.jooq.Record, java.sql.Timestamp> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.TIMESTAMP.defaulted(true), this, "");

	/**
	 * The column <code>public.event.event_body</code>.
	 */
	public final org.jooq.TableField<org.jooq.Record, java.lang.Object> EVENT_BODY = createField("event_body", org.jooq.impl.DefaultDataType.getDefaultDataType("jsonb"), this, "");

	/**
	 * Create a <code>public.event</code> table reference
	 */
	public Event() {
		this("event", null);
	}

	/**
	 * Create an aliased <code>public.event</code> table reference
	 */
	public Event(java.lang.String alias) {
		this(alias, com.quizzes.api.common.tables.Event.EVENT);
	}

	private Event(java.lang.String alias, org.jooq.Table<org.jooq.Record> aliased) {
		this(alias, aliased, null);
	}

	private Event(java.lang.String alias, org.jooq.Table<org.jooq.Record> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, com.quizzes.api.common.Public.PUBLIC, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public com.quizzes.api.common.tables.Event as(java.lang.String alias) {
		return new com.quizzes.api.common.tables.Event(alias, this);
	}

	/**
	 * Rename this table
	 */
	public com.quizzes.api.common.tables.Event rename(java.lang.String name) {
		return new com.quizzes.api.common.tables.Event(name, null);
	}
}
