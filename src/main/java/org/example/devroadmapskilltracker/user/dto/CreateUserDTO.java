package org.example.devroadmapskilltracker.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserDTO(
        @NotBlank(message = "A name is required") String fullName,
        @NotBlank(message = "A username is required") String username,
        @Size(min = 8, message = "Password must be at least 8 characters") String password){
}
