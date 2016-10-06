package com.quizzes.api.common.model;


import org.hibernate.dialect.PostgreSQL9Dialect;

import java.sql.Types;

/**
 * Hibernate’s PostgreSQL dialect to support the JSONB datatype.
 * For references check this URL out: http://www.thoughts-on-java.org/persist-postgresqls-jsonb-data-type-hibernate
 */
public class JsonbPostgreSQL9Dialect extends PostgreSQL9Dialect {

    public JsonbPostgreSQL9Dialect() {
        this.registerColumnType(Types.JAVA_OBJECT, "jsonb");
    }

}
