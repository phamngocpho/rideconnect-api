package com.rideconnect.controller.admin;

import com.rideconnect.entity.PaymentMethod;
import com.rideconnect.entity.User;
import com.rideconnect.service.PaymentMethodService;
import com.rideconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/payment-methods")
public class PaymentMethodAdminController {

    private final PaymentMethodService paymentMethodService;
    private final UserService userService;

    @Autowired
    public PaymentMethodAdminController(PaymentMethodService paymentMethodService, UserService userService) {
        this.paymentMethodService = paymentMethodService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<PaymentMethod>> getAllPaymentMethods() {
        List<PaymentMethod> methods = paymentMethodService.findAll();
        return ResponseEntity.ok(methods);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentMethod> getPaymentMethodById(@PathVariable UUID id) {
        PaymentMethod method = paymentMethodService.findById(id);
        return ResponseEntity.ok(method);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentMethod>> getPaymentMethodsByUser(@PathVariable UUID userId) {
        User user = userService.findById(userId);
        List<PaymentMethod> methods = paymentMethodService.findByUser(user);
        return ResponseEntity.ok(methods);
    }

    @GetMapping("/user/{userId}/default")
    public ResponseEntity<PaymentMethod> getDefaultPaymentMethod(@PathVariable UUID userId) {
        User user = userService.findById(userId);
        Optional<PaymentMethod> method = paymentMethodService.findByUserAndIsDefault(user, true);
        return method.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}/type/{type}")
    public ResponseEntity<List<PaymentMethod>> getPaymentMethodsByUserAndType(
            @PathVariable UUID userId,
            @PathVariable String type) {

        User user = userService.findById(userId);
        List<PaymentMethod> methods = paymentMethodService.findByUserAndType(user, type);
        return ResponseEntity.ok(methods);
    }

    @PostMapping("/add-card")
    public ResponseEntity<PaymentMethod> addCardPaymentMethod(
            @RequestParam UUID userId,
            @RequestParam String cardNumber,
            @RequestParam String cardHolderName,
            @RequestParam String expiryDate,
            @RequestParam(defaultValue = "false") Boolean isDefault) {

        User user = userService.findById(userId);
        PaymentMethod method = paymentMethodService.addCardPaymentMethod(
                user, cardNumber, cardHolderName, expiryDate, isDefault);
        return ResponseEntity.status(HttpStatus.CREATED).body(method);
    }

    @PostMapping("/add-wallet")
    public ResponseEntity<PaymentMethod> addWalletPaymentMethod(
            @RequestParam UUID userId,
            @RequestParam String walletId,
            @RequestParam String walletName,
            @RequestParam(defaultValue = "false") Boolean isDefault) {

        User user = userService.findById(userId);
        PaymentMethod method = paymentMethodService.addWalletPaymentMethod(
                user, walletId, walletName, isDefault);
        return ResponseEntity.status(HttpStatus.CREATED).body(method);
    }

    @PutMapping("/{id}/set-default")
    public ResponseEntity<Void> setDefaultPaymentMethod(
            @PathVariable UUID id,
            @RequestParam UUID userId) {

        User user = userService.findById(userId);
        paymentMethodService.setDefaultPaymentMethod(id, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentMethod(@PathVariable UUID id) {
        paymentMethodService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
