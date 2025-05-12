package com.rideconnect.controller;

import com.rideconnect.dto.request.auth.RegisterRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "share/about";
    }
    
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, 
                        @RequestParam(required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không đúng");
        }
        
        if (logout != null) {
            model.addAttribute("logoutMessage", "Bạn đã đăng xuất thành công");
        }
        
        return "auth/login";
    }
    
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        
        // Chuyển hướng dựa trên vai trò người dùng
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/admin";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_DRIVER"))) {
            return "redirect:/driver";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CUSTOMER"))) {
            return "redirect:/customer";
        }
        
        // Nếu không có vai trò cụ thể, hiển thị trang index
        return "index";
    }
    
    @GetMapping("/403")
    public String accessDenied() {
        return "error/403";
    }
    
    @GetMapping("/404")
    public String pageNotFound() {
        return "error/404";
    }
}
