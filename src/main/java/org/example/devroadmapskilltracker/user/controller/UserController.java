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

    @PostMapping("/update")
    public String updateAccount(@Valid @ModelAttribute("user")UpdateUserDTO dto,
                                BindingResult bindingResult,
                                Model model,
                                Principal principal) {

        if (bindingResult.hasErrors()) {
            return "users/account";
        }

        try {
            UserDTO currentUser = userService.getLoggedInUser(principal.getName());
            userService.updateUserAccount(currentUser.id(), dto);
        } catch (Exception ex) {
            bindingResult.rejectValue("username", "error.user", "Could not update account.");
            return "users/account";
        }

        return "redirect:/account?success";
    }


    @DeleteMapping("/delete")
    public String deleteAccount( HttpServletRequest request, Principal principal) throws ServletException {

        UserDTO currentUser = userService.getLoggedInUser(principal.getName());
        userService.deleteUserAccount(currentUser.id());

        request.logout();
        return "redirect:/login?deleted";
    }
}
