package com.rideconnect.service;

import com.rideconnect.entity.PaymentMethod;
import com.rideconnect.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentMethodService {

    PaymentMethod save(PaymentMethod paymentMethod);

    PaymentMethod findById(UUID methodId);

    List<PaymentMethod> findAll();

    List<PaymentMethod> findByUser(User user);

    Optional<PaymentMethod> findByUserAndIsDefault(User user, Boolean isDefault);

    List<PaymentMethod> findByUserAndType(User user, String type);

    Optional<PaymentMethod> findByUserAndCardNumber(User user, String cardNumber);

    Optional<PaymentMethod> findByUserAndWalletId(User user, String walletId);

    PaymentMethod addCardPaymentMethod(User user, String cardNumber, String cardHolderName, String expiryDate, Boolean isDefault);

    PaymentMethod addWalletPaymentMethod(User user, String walletId, String walletName, Boolean isDefault);

    void setDefaultPaymentMethod(UUID methodId, User user);

    void delete(UUID methodId);
}
