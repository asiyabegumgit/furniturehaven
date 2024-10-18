package org.perscholas.furniturehaven.controller;
import lombok.extern.slf4j.Slf4j;
import org.perscholas.furniturehaven.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Slf4j
@Controller
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password"; // Thymeleaf template
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, Model model) {
        log.info("Processing password reset form wih email: {}" , email);
       try {
           log.info("Processing password reset form email: {}", email);
           passwordResetService.sendResetLink(email);
           log.info("Password reset link sent");
           model.addAttribute("message", "Check your email! We have sent password recovery instructions to your email");
       }

       catch (Exception e) {
           log.error(e.getMessage());
           model.addAttribute("errorMessage", e.getMessage());
        }
        return "forgot-password"; // Return to the same template
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password"; // Thymeleaf template
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token, @RequestParam String newPassword, Model model) {
        passwordResetService.resetPassword(token, newPassword);
        model.addAttribute("message", "Password reset successfully");
        return "login"; // Redirect to login or another page
    }
}