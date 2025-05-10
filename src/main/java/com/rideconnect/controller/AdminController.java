package com.rideconnect.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @GetMapping("")
    public String dashboard(Model model) {
        // Thêm dữ liệu vào model nếu cần
        return "admin/dashboard";
    }
    
    @GetMapping("/users")
    public String users(Model model) {
        return "admin/users/index";
    }
    
    @GetMapping("/reports")
    public String reports(Model model) {
        return "admin/reports";
    }
    
    @GetMapping("/settings")
    public String settings(Model model) {
        return "admin/settings";
    }
}