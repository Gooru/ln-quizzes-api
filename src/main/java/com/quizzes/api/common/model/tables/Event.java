/**
 * This class is generated by jOOQ
 */
package com.quizzes.api.common.model.tables;

import com.quizzes.api.common.model.Public;
import com.quizzes.api.common.model.tables.records.EventRecord;

/**
 * This class is generated by jOOQ.
 */
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Event extends org.jooq.impl.TableImpl<EventRecord> {

	private static final long serialVersionUID = -616466219;

	/**
	 * The singleton instance of <code>public.event</code>
	 */
	public static final Event EVENT = new Event();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<EventRecord> getRecordType() {
		return EventRecord.class;
	}

	/**
	 * The column <code>public.event.id</code>.
	 */
	public final org.jooq.TableField<EventRecord, java.util.UUID> ID = createField("id", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

	/**
	 * The column <code>public.event.event_index</code>.
	 */
	public final org.jooq.TableField<EventRecord, java.util.UUID> EVENT_INDEX = createField("event_index", org.jooq.impl.SQLDataType.UUID, this, "");

	/**
	 * The column <code>public.event.created_at</code>.
	 */
	public final org.jooq.TableField<EventRecord, java.sql.Timestamp> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.TIMESTAMP.defaulted(true), this, "");

	/**
	 * The column <code>public.event.event_body</code>.
	 */
	public final org.jooq.TableField<EventRecord, java.lang.Object> EVENT_BODY = createField("event_body", org.jooq.impl.DefaultDataType.getDefaultDataType("jsonb"), this, "");

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
		this(alias, Event.EVENT);
	}

	private Event(java.lang.String alias, org.jooq.Table<EventRecord> aliased) {
		this(alias, aliased, null);
	}

	private Event(java.lang.String alias, org.jooq.Table<EventRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, Public.PUBLIC, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Event as(java.lang.String alias) {
		return new Event(alias, this);
	}

	/**
	 * Rename this table
	 */
	public Event rename(java.lang.String name) {
		return new Event(name, null);
	}
}
