package org.example.devroadmapskilltracker.user.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateUserDTO(
        @NotBlank(message = "A name is required") String FullName,
        @NotBlank(message = "A username is required") String UserName,
        @NotBlank(message = "A password is required") String password){
}
