package com.rideconnect.config;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.postgis.PGgeometry;
import org.postgis.Point;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PointType implements UserType<Point> {

    @Override
    public int getSqlType() {
        return java.sql.Types.OTHER;
    }

    @Override
    public Class<Point> returnedClass() {
        return Point.class;
    }

    @Override
    public boolean equals(Point x, Point y) {
        if (x == y) return true;
        if (x == null || y == null) return false;
        return x.equals(y);
    }

    @Override
    public int hashCode(Point x) {
        return x.hashCode();
    }

    @Override
    public Point nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner)
            throws SQLException {
        PGgeometry geom = (PGgeometry) rs.getObject(position);
        return geom == null ? null : (Point) geom.getGeometry();
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Point value, int index, SharedSessionContractImplementor session)
            throws SQLException {
        if (value == null) {
            st.setNull(index, java.sql.Types.OTHER);
        } else {
            value.setSrid(4326);
            PGgeometry geom = new PGgeometry(value);
            st.setObject(index, geom);
        }
    }

    @Override
    public Point deepCopy(Point value) {
        if (value == null) return null;
        return new Point(value.x, value.y);
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(Point value) {
        return (Serializable) deepCopy(value);
    }

    @Override
    public Point assemble(Serializable cached, Object owner) {
        return deepCopy((Point) cached);
    }

    @Override
    public Point replace(Point detached, Point managed, Object owner) {
        return deepCopy(detached);
    }
}
