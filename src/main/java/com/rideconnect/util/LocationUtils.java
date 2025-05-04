package com.rideconnect.util;

import org.postgis.Point;
import org.springframework.stereotype.Component;

@Component
public class LocationUtils {

    private static final int SRID = 4326; // WGS84

    /**
     * Creates a Point geometry from latitude and longitude
     */
    public Point createPoint(double latitude, double longitude) {
        // Tạo point với SRID 4326 (WGS84)
        Point point = new Point(longitude, latitude);
        point.setSrid(4326);
        return point;
    }

    /**
     * Calculates the distance between two points in meters
     */
    public double calculateDistance(Point point1, Point point2) {
        // Implementation using Haversine formula
        double lat1 = point1.getY();
        double lon1 = point1.getX();
        double lat2 = point2.getY();
        double lon2 = point2.getX();

        final int R = 6371; // Radius of the earth in km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        // convert to meters

        return R * c * 1000;
    }

    /**
     * Estimates travel time between two points based on distance and average speed
     * @return estimated time in seconds
     */
    public int estimateTravelTime(Point point1, Point point2, double averageSpeedKmh) {
        double distance = calculateDistance(point1, point2); // in meters
        double speedMs = averageSpeedKmh * 1000 / 3600; // convert km/h to m/s
        return (int) (distance / speedMs); // time in seconds
    }
}
