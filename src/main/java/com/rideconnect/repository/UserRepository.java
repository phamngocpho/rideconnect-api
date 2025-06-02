package com.rideconnect.repository;

import com.rideconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // Tìm user bằng email hoặc phone number
    @Query("SELECT u FROM User u WHERE u.email = :identifier OR u.phoneNumber = :identifier")
    Optional<User> findByEmailOrPhoneNumber(@Param("identifier") String identifier);

    // Tìm user bằng phone number
    Optional<User> findByPhoneNumber(String phoneNumber);

    // Tìm user bằng email
    Optional<User> findByEmail(String email);

    // Kiểm tra phone number đã tồn tại
    boolean existsByPhoneNumber(String phoneNumber);

    // Kiểm tra email đã tồn tại
    boolean existsByEmail(String email);
}
