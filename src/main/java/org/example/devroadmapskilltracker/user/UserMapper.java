package org.example.devroadmapskilltracker.user;

import org.example.devroadmapskilltracker.user.dto.CreateUserDTO;
import org.example.devroadmapskilltracker.user.dto.UpdateUserDTO;
import org.example.devroadmapskilltracker.user.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    // Entity --> DTO
    public UserDTO toDTO(User user) {
        if (user == null) return null;

        return new UserDTO(
                user.getId(),
                user.getFullName(),
                user.getUsername()
        );
    }

    public User toEntity(CreateUserDTO dto) {
        if (dto == null) return null;

        User user = new User();
        user.setFullName(dto.fullName());
        user.setUsername(dto.username());
        user.setPassword(dto.password());
        return user;
    }

    public void updateEntityFromDTO(UpdateUserDTO dto, User user) {
        user.setFullName(dto.fullName());
        user.setUsername(dto.username());
    }
}
