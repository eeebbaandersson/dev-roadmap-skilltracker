package org.example.devroadmapskilltracker.user.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.example.devroadmapskilltracker.skill.SkillRepository;
import org.example.devroadmapskilltracker.user.User;
import org.example.devroadmapskilltracker.user.UserMapper;
import org.example.devroadmapskilltracker.user.UserRepository;
import org.example.devroadmapskilltracker.user.dto.CreateUserDTO;
import org.example.devroadmapskilltracker.user.dto.UpdateUserDTO;
import org.example.devroadmapskilltracker.user.dto.UserDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    private static final String USER_NOT_FOUND = "User not found";

    public UserService(UserRepository userRepository, SkillRepository skillRepository, UserMapper userMapper,  PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserDTO createUserAccount(CreateUserDTO dto) {

      // Check to see if a username is already occupied
     if (userRepository.existsByUsername(dto.username())) {
         throw new IllegalArgumentException("A user with username: " + dto.username() + " already exists.");
     }

        User newUser = userMapper.toEntity(dto);
        newUser.setPassword(passwordEncoder.encode(dto.password()));

        User savedUser = userRepository.save(newUser);
        log.info("Created user account for user with id {}", savedUser.getId());
        return userMapper.toDTO(savedUser);
    }

    @Transactional
    public UserDTO updateUserAccount(Long id, UpdateUserDTO dto) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        userMapper.updateEntityFromDTO(dto, existingUser);

        if (dto.password() != null && !dto.password().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(dto.password()));
        }

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toDTO(updatedUser);
    }

    @Transactional
    public void deleteUserAccount(Long userId) {
        User existingUser  = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        skillRepository.deleteByUserId(userId);

        userRepository.delete(existingUser);
        log.info("Deleted user with id {} and all their associated skills", userId);
    }

    public UserDTO getLoggedInUser(String username) {
       User user = userRepository.findByUsername(username)
               .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND + username));
       return userMapper.toDTO(user);
    }
}
