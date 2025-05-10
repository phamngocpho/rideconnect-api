package com.rideconnect.controller;

import com.rideconnect.dto.request.auth.LoginRequest;
import com.rideconnect.dto.request.auth.RegisterRequest;
import com.rideconnect.dto.response.auth.LoginResponse;
import com.rideconnect.dto.response.auth.RegisterResponse;
import com.rideconnect.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        log.info("Showing registration form");
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setRole("ROLE_CUSTOMER"); // Set default selected role in the form
        model.addAttribute("registerRequest", registerRequest);
        return "auth/register";
    }
    
    @GetMapping("/login")
    public String showLoginForm(Model model, 
                              @RequestParam(value = "error", required = false) String error,
                              @RequestParam(value = "logout", required = false) String logout) {
        log.info("Showing login form");
        
        if (error != null) {
            model.addAttribute("errorMessage", "Số điện thoại hoặc mật khẩu không đúng");
            log.warn("Login error occurred");
        }
        
        if (logout != null) {
            model.addAttribute("logoutMessage", "Bạn đã đăng xuất thành công");
            log.info("User has logged out");
        }
        
        // Add an empty login request for form binding
        model.addAttribute("loginRequest", new LoginRequest());
        
        return "auth/login";
    }
    
    @PostMapping("/login")
    public String processLogin(@Valid @ModelAttribute("loginRequest") LoginRequest loginRequest,
                             BindingResult bindingResult,
                             Model model,
                             HttpSession session) {
        log.info("Processing login request for phone number: {}", loginRequest.getPhoneNumber());
        
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors in login form: {}", bindingResult.getAllErrors());
            return "auth/login";
        }
        
        try {
            LoginResponse response = authService.login(loginRequest);
            log.info("User logged in successfully: {}", response.getFullName());
            
            // Store user information in session if needed
            session.setAttribute("currentUser", response);
            
            // Redirect based on the role
            if (response.getRole().equals("ROLE_ADMIN")) {
                return "redirect:/admin";
            } else if (response.getRole().equals("ROLE_DRIVER")) {
                return "redirect:/driver";  // Redirect to driver home, matching DriverController route
            } else if (response.getRole().equals("ROLE_CUSTOMER")) {
                return "redirect:/customer";  // Redirect to customer home, matching URL pattern
            } else {
                return "redirect:/";  // Default fallback
            }
            
        } catch (Exception e) {
            log.error("Login failed", e);
            model.addAttribute("errorMessage", "Đăng nhập thất bại: " + e.getMessage());
            return "auth/login";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("Processing logout request");
        
        // Get authentication
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // Perform logout if authenticated
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
            log.info("User logged out successfully");
        }
        
        return "redirect:/login?logout";
    }
    
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("registerRequest") RegisterRequest request,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
    
        // Validate role selection
        if (request.getRole() == null || request.getRole().isEmpty() || 
            (!request.getRole().equals("ROLE_CUSTOMER") && !request.getRole().equals("ROLE_DRIVER"))) {
            request.setRole("ROLE_CUSTOMER");
            log.info("Invalid role provided, defaulting to: ROLE_CUSTOMER");
        }

        log.info("Processing registration request: fullName={}, phoneNumber={}, email={}, role={}",
                request.getFullName(), request.getPhoneNumber(), request.getEmail(), request.getRole());

        // Validate manually after setting defaults
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        if (!violations.isEmpty()) {
            for (ConstraintViolation<RegisterRequest> violation : violations) {
                String propertyPath = violation.getPropertyPath().toString();
                String message = violation.getMessage();
                bindingResult.rejectValue(propertyPath, "", message);
            }
        }

        // Kiểm tra lỗi validation
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors in registration form: {}", bindingResult.getAllErrors());
            return "auth/register";
        }

        // Kiểm tra mật khẩu xác nhận
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            log.warn("Password confirmation mismatch");
            model.addAttribute("error", "Mật khẩu xác nhận không khớp");
            return "auth/register";
        }

        try {
            log.info("Calling authService.register()");
            RegisterResponse response = authService.register(request);
            log.info("Registration service response: success={}, message={}", response.isSuccess(), response.getMessage());

            if (response.isSuccess()) {
                log.info("Registration successful, redirecting to login");

                // Add debug URL to see if the user was actually created
                String debugUrl = "/debug/users";
                redirectAttributes.addFlashAttribute("success",
                    "Đăng ký thành công! Vui lòng đăng nhập. " +
                    "(Debug: Kiểm tra user đã được tạo chưa tại <a href='" + debugUrl + "' target='_blank'>đây</a>)");

                // You can uncomment this to go directly to the debug page instead of login
                // return "redirect:" + debugUrl;

                return "redirect:/login";
            } else {
                log.warn("Registration failed: {}", response.getMessage());
                model.addAttribute("error", response.getMessage());
                return "auth/register";
            }
        } catch (Exception e) {
            log.error("Exception during registration process", e);
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }
}
