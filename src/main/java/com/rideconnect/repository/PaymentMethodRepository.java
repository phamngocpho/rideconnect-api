package com.rideconnect.repository;

import com.rideconnect.entity.PaymentMethod;
import com.rideconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, UUID> {

    List<PaymentMethod> findByUser(User user);

    Optional<PaymentMethod> findByUserAndIsDefault(User user, Boolean isDefault);

    List<PaymentMethod> findByUserAndType(User user, String type);

    Optional<PaymentMethod> findByUserAndCardNumber(User user, String cardNumber);

    Optional<PaymentMethod> findByUserAndWalletId(User user, String walletId);
}
