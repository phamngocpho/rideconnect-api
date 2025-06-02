package com.rideconnect.service.impl;

import com.rideconnect.entity.PaymentMethod;
import com.rideconnect.entity.User;
import com.rideconnect.repository.PaymentMethodRepository;
import com.rideconnect.service.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    @Autowired
    public PaymentMethodServiceImpl(PaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @Override
    @Transactional
    public PaymentMethod save(PaymentMethod paymentMethod) {
        return paymentMethodRepository.save(paymentMethod);
    }

    @Override
    public PaymentMethod findById(UUID methodId) {
        return paymentMethodRepository.findById(methodId)
                .orElseThrow(() -> new RuntimeException("Payment method not found with id: " + methodId));
    }

    @Override
    public List<PaymentMethod> findAll() {
        return paymentMethodRepository.findAll();
    }

    @Override
    public List<PaymentMethod> findByUser(User user) {
        return paymentMethodRepository.findByUser(user);
    }

    @Override
    public Optional<PaymentMethod> findByUserAndIsDefault(User user, Boolean isDefault) {
        return paymentMethodRepository.findByUserAndIsDefault(user, isDefault);
    }

    @Override
    public List<PaymentMethod> findByUserAndType(User user, String type) {
        return paymentMethodRepository.findByUserAndType(user, type);
    }

    @Override
    public Optional<PaymentMethod> findByUserAndCardNumber(User user, String cardNumber) {
        return paymentMethodRepository.findByUserAndCardNumber(user, cardNumber);
    }

    @Override
    public Optional<PaymentMethod> findByUserAndWalletId(User user, String walletId) {
        return paymentMethodRepository.findByUserAndWalletId(user, walletId);
    }

    @Override
    @Transactional
    public PaymentMethod addCardPaymentMethod(User user, String cardNumber, String cardHolderName, String expiryDate, Boolean isDefault) {
        if (isDefault) {
            resetDefaultPaymentMethods(user);
        }

        PaymentMethod paymentMethod = PaymentMethod.builder()
                .user(user)
                .type("card")
                .cardNumber(cardNumber)
                .cardHolderName(cardHolderName)
                .expiryDate(expiryDate)
                .isDefault(isDefault)
                .build();
        return paymentMethodRepository.save(paymentMethod);
    }

    @Override
    @Transactional
    public PaymentMethod addWalletPaymentMethod(User user, String walletId, String walletName, Boolean isDefault) {
        if (isDefault) {
            resetDefaultPaymentMethods(user);
        }

        PaymentMethod paymentMethod = PaymentMethod.builder()
                .user(user)
                .type("wallet")
                .walletId(walletId)
                .walletName(walletName)
                .isDefault(isDefault)
                .build();
        return paymentMethodRepository.save(paymentMethod);
    }

    @Override
    @Transactional
    public void setDefaultPaymentMethod(UUID methodId, User user) {
        resetDefaultPaymentMethods(user);

        PaymentMethod paymentMethod = findById(methodId);
        if (!paymentMethod.getUser().equals(user)) {
            throw new RuntimeException("Payment method does not belong to this user");
        }

        paymentMethod.setIsDefault(true);
        paymentMethodRepository.save(paymentMethod);
    }

    private void resetDefaultPaymentMethods(User user) {
        List<PaymentMethod> userPaymentMethods = paymentMethodRepository.findByUser(user);
        for (PaymentMethod method : userPaymentMethods) {
            if (Boolean.TRUE.equals(method.getIsDefault())) {
                method.setIsDefault(false);
                paymentMethodRepository.save(method);
            }
        }
    }

    @Override
    @Transactional
    public void delete(UUID methodId) {
        paymentMethodRepository.deleteById(methodId);
    }
}
