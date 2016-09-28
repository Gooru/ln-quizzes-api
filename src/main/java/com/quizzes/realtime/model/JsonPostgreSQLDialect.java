package com.quizzes.realtime.model;

import org.hibernate.dialect.PostgreSQL9Dialect;

import java.sql.Types;

/**
 * Wrap default PostgreSQL9Dialect with 'json' type.
 *
 * Created by fperez on 9/19/16.
 */
public class JsonPostgreSQLDialect extends PostgreSQL9Dialect {

    public JsonPostgreSQLDialect() {

        super();
        this.registerColumnType(Types.JAVA_OBJECT, "jsonb");
    }


}
