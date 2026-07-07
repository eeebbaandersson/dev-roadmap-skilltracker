package org.example.devroadmapskilltracker.user.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.devroadmapskilltracker.user.dto.UpdateUserDTO;
import org.example.devroadmapskilltracker.user.dto.UserDTO;
import org.example.devroadmapskilltracker.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/account")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String showAccountPage(Model model, Principal principal) {
        UserDTO user = userService.getLoggedInUser(principal.getName());

        UpdateUserDTO updateUserDTO = new UpdateUserDTO(
                user.id(),
                user.fullName(),
                user.username(),
                ""
        );

        model.addAttribute("user", updateUserDTO);
        return "users/account";
    }

    @PostMapping("/update/{id}")
    public String updateAccount(@PathVariable Long id,
                                @Valid @ModelAttribute("user")UpdateUserDTO dto,
                                BindingResult bindingResult,
                                Model model) {

        if (bindingResult.hasErrors()) {
            return "users/account";
        }

        try {
            userService.updateUserAccount(id, dto);
        } catch (Exception ex) {
            bindingResult.rejectValue("username", "error.user", "Could not update account.");
            return "users/account";
        }

        return "redirect:/account?success";
    }


    @DeleteMapping("/delete/{id}")
    public String deleteAccount(@PathVariable Long id, HttpServletRequest request) throws ServletException {
        userService.deleteUserAccount(id);
        request.logout();
        return "redirect:/login?deleted";
    }
}
