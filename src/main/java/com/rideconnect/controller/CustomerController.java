package com.rideconnect.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/customer")
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerController {

    @GetMapping("")
    public String dashboard(Model model) {
        // Thêm dữ liệu vào model nếu cần
        return "customer/dashboard";
    }
    
    @GetMapping("/profile")
    public String profile(Model model) {
        return "customer/profile";
    }
    
    @GetMapping("/bookings")
    public String bookings(Model model) {
        return "customer/bookings";
    }
    
    @GetMapping("/history")
    public String history(Model model) {
        return "customer/history";
    }
}