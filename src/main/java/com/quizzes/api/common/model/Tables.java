/**
 * This class is generated by jOOQ
 */
package com.quizzes.api.common.model;

import com.quizzes.api.common.model.tables.Collection;
import com.quizzes.api.common.model.tables.CollectionOnAir;
import com.quizzes.api.common.model.tables.Context;
import com.quizzes.api.common.model.tables.ContextProfile;
import com.quizzes.api.common.model.tables.ContextProfileEvent;
import com.quizzes.api.common.model.tables.Event;
import com.quizzes.api.common.model.tables.EventIndex;
import com.quizzes.api.common.model.tables.Group;
import com.quizzes.api.common.model.tables.GroupProfile;
import com.quizzes.api.common.model.tables.Profile;
import com.quizzes.api.common.model.tables.Resource;

/**
 * This class is generated by jOOQ.
 *
 * Convenience access to all tables in public
 */
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tables {

	/**
	 * The table public.collection
	 */
	public static final Collection COLLECTION = Collection.COLLECTION;

	/**
	 * The table public.collection_on_air
	 */
	public static final CollectionOnAir COLLECTION_ON_AIR = CollectionOnAir.COLLECTION_ON_AIR;

	/**
	 * The table public.context
	 */
	public static final com.quizzes.api.common.model.tables.Context CONTEXT = Context.CONTEXT;

	/**
	 * The table public.context_profile
	 */
	public static final ContextProfile CONTEXT_PROFILE = ContextProfile.CONTEXT_PROFILE;

	/**
	 * The table public.context_profile_event
	 */
	public static final ContextProfileEvent CONTEXT_PROFILE_EVENT = ContextProfileEvent.CONTEXT_PROFILE_EVENT;

	/**
	 * The table public.event
	 */
	public static final Event EVENT = Event.EVENT;

	/**
	 * The table public.event_index
	 */
	public static final EventIndex EVENT_INDEX = EventIndex.EVENT_INDEX;

	/**
	 * The table public.group
	 */
	public static final Group GROUP = Group.GROUP;

	/**
	 * The table public.group_profile
	 */
	public static final GroupProfile GROUP_PROFILE = GroupProfile.GROUP_PROFILE;

	/**
	 * The table public.profile
	 */
	public static final Profile PROFILE = Profile.PROFILE;

	/**
	 * The table public.resource
	 */
	public static final Resource RESOURCE = Resource.RESOURCE;
}
