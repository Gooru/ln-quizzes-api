/**
 * This class is generated by jOOQ
 */
package com.quizzes.api.common.model.tables;

import com.quizzes.api.common.model.Public;
import com.quizzes.api.common.model.StringJSONBinding;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.records.CollectionRecord;

/**
 * This class is generated by jOOQ.
 */
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Collection extends org.jooq.impl.TableImpl<CollectionRecord> {

	private static final long serialVersionUID = 967001283;

	/**
	 * The singleton instance of <code>public.collection</code>
	 */
	public static final Collection COLLECTION = new Collection();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<CollectionRecord> getRecordType() {
		return CollectionRecord.class;
	}

	/**
	 * The column <code>public.collection.id</code>.
	 */
	public final org.jooq.TableField<CollectionRecord, java.util.UUID> ID = createField("id", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

	/**
	 * The column <code>public.collection.external_id</code>.
	 */
	public final org.jooq.TableField<CollectionRecord, java.lang.String> EXTERNAL_ID = createField("external_id", org.jooq.impl.SQLDataType.VARCHAR.length(50).nullable(false), this, "");

	/**
	 * The column <code>public.collection.lms_id</code>.
	 */
	public final org.jooq.TableField<CollectionRecord, Lms> LMS_ID = createField("lms_id", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(Lms.class), this, "");

	/**
	 * The column <code>public.collection.is_collection</code>.
	 */
	public final org.jooq.TableField<CollectionRecord, java.lang.Boolean> IS_COLLECTION = createField("is_collection", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>public.collection.owner_profile_id</code>.
	 */
	public final org.jooq.TableField<CollectionRecord, java.util.UUID> OWNER_PROFILE_ID = createField("owner_profile_id", org.jooq.impl.SQLDataType.UUID.nullable(false), this, "");

	/**
	 * The column <code>public.collection.collection_data</code>.
	 */
	public final org.jooq.TableField<CollectionRecord, java.lang.String> COLLECTION_DATA = createField("collection_data", org.jooq.impl.DefaultDataType.getDefaultDataType("jsonb"), this, "", new StringJSONBinding());

	/**
	 * The column <code>public.collection.is_lock</code>.
	 */
	public final org.jooq.TableField<CollectionRecord, java.lang.Boolean> IS_LOCK = createField("is_lock", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>public.collection.is_deleted</code>.
	 */
	public final org.jooq.TableField<CollectionRecord, java.lang.Boolean> IS_DELETED = createField("is_deleted", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>public.collection.created_at</code>.
	 */
	public final org.jooq.TableField<CollectionRecord, java.sql.Timestamp> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaulted(true), this, "");

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
		this(alias, Collection.COLLECTION);
	}

	private Collection(java.lang.String alias, org.jooq.Table<CollectionRecord> aliased) {
		this(alias, aliased, null);
	}

	private Collection(java.lang.String alias, org.jooq.Table<CollectionRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, Public.PUBLIC, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection as(java.lang.String alias) {
		return new Collection(alias, this);
	}

	/**
	 * Rename this table
	 */
	public Collection rename(java.lang.String name) {
		return new Collection(name, null);
	}
}
