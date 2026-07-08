package org.example.devroadmapskilltracker.user.controller;

import jakarta.validation.Valid;
import org.example.devroadmapskilltracker.user.dto.CreateUserDTO;
import org.example.devroadmapskilltracker.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "users/login";
    }


    @GetMapping("/signup")
    public String showSignupPage(Model model) {
        model.addAttribute("user", new CreateUserDTO("", "", ""));
        return "users/signup";
    }

    @PostMapping("/createAccount")
    public String createAccount(@Valid @ModelAttribute("user") CreateUserDTO dto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "users/signup";
        }

        try {
            userService.createUserAccount(dto);
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("username", "error.user", e.getMessage());
            return "users/signup";
        } catch (Exception e) {
            bindingResult.rejectValue("username", "error.user", "An unknown error has occurred.");
            return "users/signup";
        }

        return "redirect:/login";
    }
}
