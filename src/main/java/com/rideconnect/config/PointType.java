package com.rideconnect.config;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

public class PointType implements UserType<Point> {

    @Override
    public int getSqlType() {
        return Types.OTHER;
    }

    @Override
    public Class<Point> returnedClass() {
        return Point.class;
    }

    @Override
    public boolean equals(Point x, Point y) throws HibernateException {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(Point x) throws HibernateException {
        return Objects.hashCode(x);
    }

    @Override
    public Point nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner)
            throws HibernateException, SQLException {
        String pgGeometry = rs.getString(position);
        if (pgGeometry == null) {
            return null;
        }

        try {
            // Chuyển đổi từ định dạng WKT (Well-Known Text) sang đối tượng Point của JTS
            WKTReader reader = new WKTReader();
            return (Point) reader.read(pgGeometry);
        } catch (ParseException e) {
            throw new HibernateException("Error parsing WKT point", e);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Point value, int index, SharedSessionContractImplementor session)
            throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            // Chuyển đổi từ Point của JTS sang định dạng WKT
            WKTWriter writer = new WKTWriter();
            String wkt = writer.write(value);
            st.setObject(index, "SRID=4326;" + wkt, Types.OTHER);
        }
    }

    @Override
    public Point deepCopy(Point value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Point value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Point assemble(Serializable cached, Object owner) throws HibernateException {
        return (Point) cached;
    }

    @Override
    public Point replace(Point original, Point target, Object owner) throws HibernateException {
        return original;
    }
}
