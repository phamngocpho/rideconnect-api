package com.rideconnect.repository;

import com.rideconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @NonNull
    Optional<User> findById(@NonNull UUID id);

    @NonNull
    Optional<User> findByPhoneNumber(@NonNull String phoneNumber);

    @NonNull
    Optional<User> findByEmail(@NonNull String email);

    boolean existsByPhoneNumber(@NonNull String phoneNumber);

    boolean existsByEmail(@NonNull String email);
}
