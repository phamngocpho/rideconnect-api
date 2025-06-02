package com.rideconnect.repository;

import com.rideconnect.entity.Message;
import com.rideconnect.entity.Trip;
import com.rideconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findByTrip(Trip trip);

    List<Message> findBySender(User sender);

    List<Message> findByRecipient(User recipient);

    List<Message> findByRecipientAndIsRead(User recipient, Boolean isRead);

    List<Message> findByTripOrderByCreatedAtAsc(Trip trip);

    List<Message> findBySenderAndRecipient(User sender, User recipient);

    long countByRecipientAndIsRead(User recipient, Boolean isRead);
}
