/**
 * This class is generated by jOOQ
 */
package com.quizzes.api.common.model;

/**
 * This class is generated by jOOQ.
 *
 * Convenience access to all stored procedures and functions in public
 */
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Routines {

	/**
	 * Call <code>public.uuid_generate_v1</code>
	 */
	public static java.util.UUID uuidGenerateV1(org.jooq.Configuration configuration) {
		com.quizzes.api.common.model.routines.UuidGenerateV1 f = new com.quizzes.api.common.model.routines.UuidGenerateV1();

		f.execute(configuration);
		return f.getReturnValue();
	}

	/**
	 * Get <code>public.uuid_generate_v1</code> as a field
	 */
	public static org.jooq.Field<java.util.UUID> uuidGenerateV1() {
		com.quizzes.api.common.model.routines.UuidGenerateV1 f = new com.quizzes.api.common.model.routines.UuidGenerateV1();

		return f.asField();
	}

	/**
	 * Call <code>public.uuid_generate_v1mc</code>
	 */
	public static java.util.UUID uuidGenerateV1mc(org.jooq.Configuration configuration) {
		com.quizzes.api.common.model.routines.UuidGenerateV1mc f = new com.quizzes.api.common.model.routines.UuidGenerateV1mc();

		f.execute(configuration);
		return f.getReturnValue();
	}

	/**
	 * Get <code>public.uuid_generate_v1mc</code> as a field
	 */
	public static org.jooq.Field<java.util.UUID> uuidGenerateV1mc() {
		com.quizzes.api.common.model.routines.UuidGenerateV1mc f = new com.quizzes.api.common.model.routines.UuidGenerateV1mc();

		return f.asField();
	}

	/**
	 * Call <code>public.uuid_generate_v3</code>
	 */
	public static java.util.UUID uuidGenerateV3(org.jooq.Configuration configuration, java.util.UUID namespace, java.lang.String name) {
		com.quizzes.api.common.model.routines.UuidGenerateV3 f = new com.quizzes.api.common.model.routines.UuidGenerateV3();
		f.setNamespace(namespace);
		f.setName_(name);

		f.execute(configuration);
		return f.getReturnValue();
	}

	/**
	 * Get <code>public.uuid_generate_v3</code> as a field
	 */
	public static org.jooq.Field<java.util.UUID> uuidGenerateV3(java.util.UUID namespace, java.lang.String name) {
		com.quizzes.api.common.model.routines.UuidGenerateV3 f = new com.quizzes.api.common.model.routines.UuidGenerateV3();
		f.setNamespace(namespace);
		f.setName_(name);

		return f.asField();
	}

	/**
	 * Get <code>public.uuid_generate_v3</code> as a field
	 */
	public static org.jooq.Field<java.util.UUID> uuidGenerateV3(org.jooq.Field<java.util.UUID> namespace, org.jooq.Field<java.lang.String> name) {
		com.quizzes.api.common.model.routines.UuidGenerateV3 f = new com.quizzes.api.common.model.routines.UuidGenerateV3();
		f.setNamespace(namespace);
		f.setName_(name);

		return f.asField();
	}

	/**
	 * Call <code>public.uuid_generate_v4</code>
	 */
	public static java.util.UUID uuidGenerateV4(org.jooq.Configuration configuration) {
		com.quizzes.api.common.model.routines.UuidGenerateV4 f = new com.quizzes.api.common.model.routines.UuidGenerateV4();

		f.execute(configuration);
		return f.getReturnValue();
	}

	/**
	 * Get <code>public.uuid_generate_v4</code> as a field
	 */
	public static org.jooq.Field<java.util.UUID> uuidGenerateV4() {
		com.quizzes.api.common.model.routines.UuidGenerateV4 f = new com.quizzes.api.common.model.routines.UuidGenerateV4();

		return f.asField();
	}

	/**
	 * Call <code>public.uuid_generate_v5</code>
	 */
	public static java.util.UUID uuidGenerateV5(org.jooq.Configuration configuration, java.util.UUID namespace, java.lang.String name) {
		com.quizzes.api.common.model.routines.UuidGenerateV5 f = new com.quizzes.api.common.model.routines.UuidGenerateV5();
		f.setNamespace(namespace);
		f.setName_(name);

		f.execute(configuration);
		return f.getReturnValue();
	}

	/**
	 * Get <code>public.uuid_generate_v5</code> as a field
	 */
	public static org.jooq.Field<java.util.UUID> uuidGenerateV5(java.util.UUID namespace, java.lang.String name) {
		com.quizzes.api.common.model.routines.UuidGenerateV5 f = new com.quizzes.api.common.model.routines.UuidGenerateV5();
		f.setNamespace(namespace);
		f.setName_(name);

		return f.asField();
	}

	/**
	 * Get <code>public.uuid_generate_v5</code> as a field
	 */
	public static org.jooq.Field<java.util.UUID> uuidGenerateV5(org.jooq.Field<java.util.UUID> namespace, org.jooq.Field<java.lang.String> name) {
		com.quizzes.api.common.model.routines.UuidGenerateV5 f = new com.quizzes.api.common.model.routines.UuidGenerateV5();
		f.setNamespace(namespace);
		f.setName_(name);

		return f.asField();
	}

	/**
	 * Call <code>public.uuid_nil</code>
	 */
	public static java.util.UUID uuidNil(org.jooq.Configuration configuration) {
		com.quizzes.api.common.model.routines.UuidNil f = new com.quizzes.api.common.model.routines.UuidNil();

		f.execute(configuration);
		return f.getReturnValue();
	}

	/**
	 * Get <code>public.uuid_nil</code> as a field
	 */
	public static org.jooq.Field<java.util.UUID> uuidNil() {
		com.quizzes.api.common.model.routines.UuidNil f = new com.quizzes.api.common.model.routines.UuidNil();

		return f.asField();
	}

	/**
	 * Call <code>public.uuid_ns_dns</code>
	 */
	public static java.util.UUID uuidNsDns(org.jooq.Configuration configuration) {
		com.quizzes.api.common.model.routines.UuidNsDns f = new com.quizzes.api.common.model.routines.UuidNsDns();

		f.execute(configuration);
		return f.getReturnValue();
	}

	/**
	 * Get <code>public.uuid_ns_dns</code> as a field
	 */
	public static org.jooq.Field<java.util.UUID> uuidNsDns() {
		com.quizzes.api.common.model.routines.UuidNsDns f = new com.quizzes.api.common.model.routines.UuidNsDns();

		return f.asField();
	}

	/**
	 * Call <code>public.uuid_ns_oid</code>
	 */
	public static java.util.UUID uuidNsOid(org.jooq.Configuration configuration) {
		com.quizzes.api.common.model.routines.UuidNsOid f = new com.quizzes.api.common.model.routines.UuidNsOid();

		f.execute(configuration);
		return f.getReturnValue();
	}

	/**
	 * Get <code>public.uuid_ns_oid</code> as a field
	 */
	public static org.jooq.Field<java.util.UUID> uuidNsOid() {
		com.quizzes.api.common.model.routines.UuidNsOid f = new com.quizzes.api.common.model.routines.UuidNsOid();

		return f.asField();
	}

	/**
	 * Call <code>public.uuid_ns_url</code>
	 */
	public static java.util.UUID uuidNsUrl(org.jooq.Configuration configuration) {
		com.quizzes.api.common.model.routines.UuidNsUrl f = new com.quizzes.api.common.model.routines.UuidNsUrl();

		f.execute(configuration);
		return f.getReturnValue();
	}

	/**
	 * Get <code>public.uuid_ns_url</code> as a field
	 */
	public static org.jooq.Field<java.util.UUID> uuidNsUrl() {
		com.quizzes.api.common.model.routines.UuidNsUrl f = new com.quizzes.api.common.model.routines.UuidNsUrl();

		return f.asField();
	}

	/**
	 * Call <code>public.uuid_ns_x500</code>
	 */
	public static java.util.UUID uuidNsX500(org.jooq.Configuration configuration) {
		com.quizzes.api.common.model.routines.UuidNsX500 f = new com.quizzes.api.common.model.routines.UuidNsX500();

		f.execute(configuration);
		return f.getReturnValue();
	}

	/**
	 * Get <code>public.uuid_ns_x500</code> as a field
	 */
	public static org.jooq.Field<java.util.UUID> uuidNsX500() {
		com.quizzes.api.common.model.routines.UuidNsX500 f = new com.quizzes.api.common.model.routines.UuidNsX500();

		return f.asField();
	}
}
