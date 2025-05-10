package com.rideconnect.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/driver")
@PreAuthorize("hasRole('DRIVER')")
public class DriverController {

    @GetMapping("")
    public String dashboard(Model model) {
        // Thêm dữ liệu vào model nếu cần
        return "driver/dashboard";
    }
    
    @GetMapping("/profile")
    public String profile(Model model) {
        return "driver/profile";
    }
    
    @GetMapping("/trips")
    public String trips(Model model) {
        return "driver/trips";
    }
    
    @GetMapping("/earnings")
    public String earnings(Model model) {
        return "driver/earnings";
    }
}