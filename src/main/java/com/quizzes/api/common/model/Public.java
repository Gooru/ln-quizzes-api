/**
 * This class is generated by jOOQ
 */
package com.quizzes.api.common.model;

/**
 * This class is generated by jOOQ.
 */
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Public extends org.jooq.impl.SchemaImpl {

	private static final long serialVersionUID = 82642439;

	/**
	 * The singleton instance of <code>public</code>
	 */
	public static final Public PUBLIC = new Public();

	/**
	 * No further instances allowed
	 */
	private Public() {
		super("public");
	}

	@Override
	public final java.util.List<org.jooq.Table<?>> getTables() {
		java.util.List result = new java.util.ArrayList();
		result.addAll(getTables0());
		return result;
	}

	private final java.util.List<org.jooq.Table<?>> getTables0() {
		return java.util.Arrays.<org.jooq.Table<?>>asList(
			com.quizzes.api.common.model.tables.Collection.COLLECTION,
			com.quizzes.api.common.model.tables.CollectionOnAir.COLLECTION_ON_AIR,
			com.quizzes.api.common.model.tables.Context.CONTEXT,
			com.quizzes.api.common.model.tables.ContextProfile.CONTEXT_PROFILE,
			com.quizzes.api.common.model.tables.ContextProfileEvent.CONTEXT_PROFILE_EVENT,
			com.quizzes.api.common.model.tables.Event.EVENT,
			com.quizzes.api.common.model.tables.EventIndex.EVENT_INDEX,
			com.quizzes.api.common.model.tables.Group.GROUP,
			com.quizzes.api.common.model.tables.GroupProfile.GROUP_PROFILE,
			com.quizzes.api.common.model.tables.Profile.PROFILE,
			com.quizzes.api.common.model.tables.Resource.RESOURCE);
	}
}