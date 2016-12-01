package com.quizzes.api.common.jpa;


import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;


/**
 * Customized UserType to support conversion between String and JSON to be stored in PostgreSQL 'jsonb'
 * columns type.
 * For references check this URL out: http://www.thoughts-on-java.org/persist-postgresqls-jsonb-data-type-hibernate
 *
 * @see org.hibernate.usertype.UserType
 */

public class StringJsonType implements UserType {

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.JAVA_OBJECT};
    }

    @Override
    public Class<String> returnedClass() {
        return String.class;
    }

    @Override
    public boolean equals(final Object x, final Object y) throws HibernateException {
        if (x == null) {
            return y == null;
        }
        return x.equals(y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(final ResultSet rs, final String[] names, final SessionImplementor session,
                              Object owner) throws HibernateException, SQLException {
        final String value = rs.getString(names[0]);
        // A Mapping logic could go here...
        return value;
    }

    @Override
    public void nullSafeSet(final PreparedStatement st, final Object value, final int index,
                            final SessionImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
            return;
        }
        // A Mapping logic could go here...
        st.setObject(index, value.toString(), Types.OTHER);
    }

    @Override
    public Object deepCopy(final Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return this.deepCopy(value).toString();
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return this.deepCopy(cached);
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

}
