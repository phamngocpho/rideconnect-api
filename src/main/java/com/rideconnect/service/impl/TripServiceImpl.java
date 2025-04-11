package com.rideconnect.service.impl;

import com.rideconnect.dto.request.trip.CreateTripRequest;
import com.rideconnect.dto.request.trip.UpdateTripStatusRequest;
import com.rideconnect.dto.response.trip.TripDetailsResponse;
import com.rideconnect.dto.response.trip.TripHistoryResponse;
import com.rideconnect.entity.*;
import com.rideconnect.exception.BadRequestException;
import com.rideconnect.exception.ResourceNotFoundException;
import com.rideconnect.repository.*;
import com.rideconnect.service.TripService;
import com.rideconnect.util.LocationUtils;
import com.rideconnect.util.PriceCalculator;
import com.rideconnect.websocket.WebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.postgis.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final DriverRepository driverRepository;
    private final DriverLocationRepository driverLocationRepository;
    private final NotificationRepository notificationRepository;
    private final LocationUtils locationUtils;
    private final PriceCalculator priceCalculator;
    private final WebSocketHandler webSocketHandler;

    @Override
    @Transactional
    public TripDetailsResponse createTrip(String userId, CreateTripRequest request) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Customer customer = customerRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", userId));

        // Create location points
        Point pickupLocation = locationUtils.createPoint(request.getPickupLatitude(), request.getPickupLongitude());
        Point dropoffLocation = locationUtils.createPoint(request.getDropoffLatitude(), request.getDropoffLongitude());

        // Calculate distance and duration
        float distanceInKm = (float) (locationUtils.calculateDistance(pickupLocation, dropoffLocation) / 1000);
        int durationInMinutes = (int) (distanceInKm * 2); // Rough estimate: 2 minutes per km

        // Calculate estimated fare
        BigDecimal estimatedFare = priceCalculator.calculateFare(distanceInKm, durationInMinutes, request.getVehicleType());

        // Find a driver (either preferred or nearest available)
        Driver driver;
        if (request.getPreferredDriverId() != null) {
            driver = driverRepository.findById(request.getPreferredDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", request.getPreferredDriverId().toString()));
        } else {
            // Find nearest available driver with matching vehicle type
            List<Object[]> nearbyDrivers = driverLocationRepository.findAvailableDriversWithinRadius(
                    request.getPickupLatitude(),  // latitude
                    request.getPickupLongitude(), // longitude
                    5000.0,                      // radius (as double)
                    request.getVehicleType());

            if (nearbyDrivers.isEmpty()) {
                throw new BadRequestException("No available drivers found nearby");
            }

            // Get the nearest driver
            Object[] nearest = nearbyDrivers.get(0);
            DriverLocation driverLocation = (DriverLocation) nearest[0];
            driver = driverLocation.getDriver();
        }

        // Create trip
        Trip trip = Trip.builder()
                .customer(customer)
                .driver(driver)
                .pickupLocation(pickupLocation)
                .dropoffLocation(dropoffLocation)
                .pickupAddress(request.getPickupAddress())
                .dropoffAddress(request.getDropoffAddress())
                .status("pending")
                .vehicleType(request.getVehicleType())
                .estimatedDistance(distanceInKm)
                .estimatedDuration(durationInMinutes)
                .estimatedFare(estimatedFare)
                .build();

        Trip savedTrip = tripRepository.save(trip);

        // Create notification for driver
        Notification driverNotification = Notification.builder()
                .user(driver.getUser())
                .type("new_trip_request")
                .title("New Trip Request")
                .message("You have a new trip request from " + customer.getUser().getFullName())
                .relatedId(savedTrip.getTripId())
                .isRead(false)
                .build();
        notificationRepository.save(driverNotification);

        // Send trip request to driver via WebSocket
        webSocketHandler.sendTripRequestToDriver(driver.getDriverId().toString(),
                TripDetailsResponse.builder()
                        .tripId(savedTrip.getTripId())
                        .customerId(customer.getCustomerId())
                        .customerName(customer.getUser().getFullName())
                        .customerPhone(customer.getUser().getPhoneNumber())
                        .pickupLatitude(request.getPickupLatitude())
                        .pickupLongitude(request.getPickupLongitude())
                        .pickupAddress(request.getPickupAddress())
                        .dropoffLatitude(request.getDropoffLatitude())
                        .dropoffLongitude(request.getDropoffLongitude())
                        .dropoffAddress(request.getDropoffAddress())
                        .status("pending")
                        .estimatedDistance(distanceInKm)
                        .estimatedDuration(durationInMinutes)
                        .estimatedFare(estimatedFare)
                        .build());

        return mapTripToTripDetailsResponse(savedTrip);
    }

    @Override
    @Transactional(readOnly = true)
    public TripDetailsResponse getTripDetails(String userId, UUID tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId.toString()));

        // Check if user is either customer or driver of this trip
        UUID userUuid = UUID.fromString(userId);
        if (!trip.getCustomer().getCustomerId().equals(userUuid) &&
                !trip.getDriver().getDriverId().equals(userUuid)) {
            throw new BadRequestException("You are not authorized to view this trip");
        }

        return mapTripToTripDetailsResponse(trip);
    }

    @Override
    @Transactional
    public TripDetailsResponse updateTripStatus(String userId, UUID tripId, UpdateTripStatusRequest request) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId.toString()));

        // Check if user is either customer or driver of this trip
        UUID userUuid = UUID.fromString(userId);
        boolean isDriver = trip.getDriver().getDriverId().equals(userUuid);
        boolean isCustomer = trip.getCustomer().getCustomerId().equals(userUuid);

        if (!isDriver && !isCustomer) {
            throw new BadRequestException("You are not authorized to update this trip");
        }

        // Validate status transition
        validateStatusTransition(trip.getStatus(), request.getStatus(), isDriver, isCustomer);

        // Update trip status
        trip.setStatus(request.getStatus());

        // Set timestamps based on status
        switch (request.getStatus()) {
            case "accepted":
                trip.setAcceptedAt(ZonedDateTime.now());
                break;
            case "started":
                trip.setStartedAt(ZonedDateTime.now());
                break;
            case "completed":
                trip.setCompletedAt(ZonedDateTime.now());
                break;
            case "cancelled":
                trip.setCancelledAt(ZonedDateTime.now());
                trip.setCancellationReason(request.getCancellationReason());
                break;
        }

        Trip updatedTrip = tripRepository.save(trip);

        // Create notification for the other party
        User recipient = isDriver ? trip.getCustomer().getUser() : trip.getDriver().getUser();
        String title = "Trip " + request.getStatus();
        String message = "Your trip has been " + request.getStatus();

        if ("cancelled".equals(request.getStatus())) {
            message += ". Reason: " + request.getCancellationReason();
        }

        Notification notification = Notification.builder()
                .user(recipient)
                .type("trip_status_update")
                .title(title)
                .message(message)
                .relatedId(tripId)
                .build();
        notificationRepository.save(notification);

        // Send update via WebSocket
        TripDetailsResponse response = mapTripToTripDetailsResponse(updatedTrip);
        if (isDriver) {
            webSocketHandler.sendLocationUpdateToCustomer(trip.getCustomer().getCustomerId().toString(), response);
        } else {
            webSocketHandler.sendTripRequestToDriver(trip.getDriver().getDriverId().toString(), response);
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public TripHistoryResponse getTripHistory(String userId) {
        UUID userUuid = UUID.fromString(userId);
        List<Trip> trips = tripRepository.findTripsByUserIdOrderByCreatedAtDesc(userUuid);

        // Tạo bản sao final của userUuid để sử dụng trong lambda
        final UUID finalUserUuid = userUuid;

        List<TripHistoryResponse.TripSummary> tripSummaries = trips.stream()
                .map(trip -> TripHistoryResponse.TripSummary.builder()
                        .tripId(trip.getTripId())
                        .pickupAddress(trip.getPickupAddress())
                        .dropoffAddress(trip.getDropoffAddress())
                        .status(trip.getStatus())
                        .createdAt(trip.getCreatedAt())
                        .completedAt(trip.getCompletedAt())
                        .fare("completed".equals(trip.getStatus()) ? trip.getActualFare() : trip.getEstimatedFare())
                        .vehicleType(trip.getVehicleType())
                        // Sửa đoạn code lấy rating
                        .rating(trip.getRatings() != null ? trip.getRatings().stream()
                                .filter(rating -> rating.getRater() != null &&
                                        rating.getRater().getUserId().equals(finalUserUuid))
                                .map(Rating::getRatingValue)
                                .findFirst()
                                .orElse(null) : null)
                        .build())
                .collect(Collectors.toList());

        return TripHistoryResponse.builder()
                .trips(tripSummaries)
                .build();
    }


    private TripDetailsResponse mapTripToTripDetailsResponse(Trip trip) {
        TripDetailsResponse.DriverLocationDto driverLocationDto = null;

        // Get driver's current location if available
        if (trip.getDriver() != null) {
            driverLocationDto = driverLocationRepository.findById(trip.getDriver().getDriverId())
                    .map(driverLocation -> {
                        Point location = driverLocation.getCurrentLocation();
                        return TripDetailsResponse.DriverLocationDto.builder()
                                .latitude(location.getY())
                                .longitude(location.getX())
                                .heading(driverLocation.getHeading())
                                .lastUpdated(driverLocation.getLastUpdated())
                                .build();
                    })
                    .orElse(null);
        }

        return TripDetailsResponse.builder()
                .tripId(trip.getTripId())
                .customerId(trip.getCustomer().getCustomerId())
                .customerName(trip.getCustomer().getUser().getFullName())
                .customerPhone(trip.getCustomer().getUser().getPhoneNumber())
                .driverId(trip.getDriver().getDriverId())
                .driverName(trip.getDriver().getUser().getFullName())
                .driverPhone(trip.getDriver().getUser().getPhoneNumber())
                .vehicleType(trip.getVehicleType())
                .vehiclePlate(trip.getDriver().getVehiclePlate())
                .pickupLatitude(trip.getPickupLocation().getY())
                .pickupLongitude(trip.getPickupLocation().getX())
                .pickupAddress(trip.getPickupAddress())
                .dropoffLatitude(trip.getDropoffLocation().getY())
                .dropoffLongitude(trip.getDropoffLocation().getX())
                .dropoffAddress(trip.getDropoffAddress())
                .status(trip.getStatus())
                .estimatedDistance(trip.getEstimatedDistance())
                .estimatedDuration(trip.getEstimatedDuration())
                .estimatedFare(trip.getEstimatedFare())
                .actualFare(trip.getActualFare())
                .createdAt(trip.getCreatedAt())
                .startedAt(trip.getStartedAt())
                .completedAt(trip.getCompletedAt())
                .cancelledAt(trip.getCancelledAt())
                .cancellationReason(trip.getCancellationReason())
                .driverLocation(driverLocationDto)
                .build();
    }

    private void validateStatusTransition(String currentStatus, String newStatus, boolean isDriver, boolean isCustomer) {
        switch (currentStatus) {
            case "pending":
                if ("accepted".equals(newStatus)) {
                    if (!isDriver) {
                        throw new BadRequestException("Only driver can accept a trip");
                    }
                } else if ("cancelled".equals(newStatus)) {
                    // Both customer and driver can cancel a pending trip
                } else {
                    throw new BadRequestException("Invalid status transition from pending to " + newStatus);
                }
                break;
            case "accepted":
                if ("started".equals(newStatus)) {
                    if (!isDriver) {
                        throw new BadRequestException("Only driver can start a trip");
                    }
                } else if ("cancelled".equals(newStatus)) {
                    // Both customer and driver can cancel an accepted trip
                } else {
                    throw new BadRequestException("Invalid status transition from accepted to " + newStatus);
                }
                break;
            case "started":
                if ("completed".equals(newStatus)) {
                    if (!isDriver) {
                        throw new BadRequestException("Only driver can complete a trip");
                    }
                } else if ("cancelled".equals(newStatus)) {
                    throw new BadRequestException("Cannot cancel a started trip");
                } else {
                    throw new BadRequestException("Invalid status transition from started to " + newStatus);
                }
                break;
            case "completed":
            case "cancelled":
                throw new BadRequestException("Cannot change status of a " + currentStatus + " trip");
        }
    }
}

