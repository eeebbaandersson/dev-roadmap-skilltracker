package org.example.devroadmapskilltracker.user.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateUserDTO(
        @NotBlank(message = "A name is required") String fullName,
        @NotBlank(message = "A username is required") String username,
        @NotBlank(message = "A password is required") String password){
}
