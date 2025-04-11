package com.rideconnect.repository;

import com.rideconnect.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findByTripTripIdOrderByCreatedAt(UUID tripId);

    List<Message> findByRecipientUserIdAndIsReadFalse(UUID recipientId);

    long countByRecipientUserIdAndIsReadFalse(UUID recipientId);
}
