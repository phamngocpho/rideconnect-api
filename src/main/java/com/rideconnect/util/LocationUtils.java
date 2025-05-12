package com.rideconnect.util;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Component;

@Component
public class LocationUtils {

    private static final int SRID = 4326; // WGS84
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), SRID);

    public Point createPoint(double latitude, double longitude) {
        // JTS sử dụng thứ tự (longitude, latitude) khác với PostGIS
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }

    /**
     * Tính khoảng cách giữa hai điểm theo mét sử dụng công thức Haversine
     */
    public double calculateDistance(Point point1, Point point2) {
        final int R = 6371000; // Bán kính trái đất tính bằng mét

        double lat1 = point1.getY() * Math.PI / 180;
        double lon1 = point1.getX() * Math.PI / 180;
        double lat2 = point2.getY() * Math.PI / 180;
        double lon2 = point2.getX() * Math.PI / 180;

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return R * c;
    }
}
