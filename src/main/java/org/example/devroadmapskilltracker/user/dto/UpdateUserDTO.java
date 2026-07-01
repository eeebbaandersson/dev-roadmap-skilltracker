package org.example.devroadmapskilltracker.user.dto;

public record UpdateUserDTO(
        Long id,
        String fullName,
        String username,
        String password ) {
}
