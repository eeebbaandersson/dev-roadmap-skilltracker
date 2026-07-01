package org.example.devroadmapskilltracker.skill.service;

import org.example.devroadmapskilltracker.skill.Skill;
import org.example.devroadmapskilltracker.skill.SkillRepository;
import org.example.devroadmapskilltracker.skill.SkillStatus;
import org.example.devroadmapskilltracker.skill.dto.CreateSkillDTO;
import org.example.devroadmapskilltracker.skill.dto.SkillDTO;
import org.example.devroadmapskilltracker.skill.dto.UpdateSkillDTO;
import org.example.devroadmapskilltracker.skill.exception.ResourceNotFoundException;
import org.example.devroadmapskilltracker.user.User;
import org.example.devroadmapskilltracker.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SkillService {

    private static final String NOT_FOUND_MESSAGE = "Error: Could not find skill with id: ";

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final UserRepository userRepository;

    public SkillService(SkillRepository skillRepository, SkillMapper skillMapper, UserRepository userRepository) {
        this.skillRepository = skillRepository;
        this.skillMapper = skillMapper;
        this.userRepository = userRepository;
    }

    public SkillDTO getSkillById(Long id) {
     User currentUser = getCurrentUser();
     Skill skill = skillRepository.findById(id)
             .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE + id));

     if (!skill.getUser().getId().equals(currentUser.getId())) {
         throw new org.springframework.security.access.AccessDeniedException("Not authorized to view this skill");
     }
     return skillMapper.toDTO(skill);

    }

    public Page<SkillDTO> getSkills(String title, String tag, Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<Skill> result;

        if ((title == null || title.isBlank()) && (tag == null || tag.isBlank())) {
            result = skillRepository.findAllByUserId(currentUser.getId(), pageable);
        } else if (tag == null || tag.isBlank()) {
            result = skillRepository.findByTitleContainingIgnoreCaseAndUserId(title, currentUser.getId(), pageable);
        } else {
            result = skillRepository.findByTitleContainingIgnoreCaseOrTagIgnoreCaseAndUserId(title, tag, currentUser.getId(), pageable);
        }

        return result.map(skillMapper::toDTO);
    }

    @Transactional
    public SkillDTO createSkill(CreateSkillDTO dto) {
        User currentUser = getCurrentUser();

        if (skillRepository.existsByTitleAndUserId(dto.title(), currentUser.getId())) {
            throw new IllegalArgumentException("A skill with title: " + dto.title() + " already exists.");
        }

        Skill skillEntity = skillMapper.toEntity(dto);
        skillEntity.setUser(currentUser);

        if (skillEntity.getStatus() == SkillStatus.MASTERED) {
            skillEntity.setCompletedAt(LocalDateTime.now());
        } else {
            skillEntity.setCompletedAt(null);
        }

        Skill savedSkill = skillRepository.save(skillEntity);
        return skillMapper.toDTO(savedSkill);
    }


    @Transactional
    public SkillDTO updateSkill(Long id, UpdateSkillDTO dto) {
        User currentUser = getCurrentUser();

        Skill existingSkill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE + id));

        if (!existingSkill.getUser().getId().equals(currentUser.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Not authorized to update this skill");
        }

        skillRepository.findByTitleIgnoreCaseAndUserId(dto.title(), currentUser.getId()).ifPresent(foundSkill -> {
            if (!foundSkill.getId().equals(id)) {
                throw new IllegalArgumentException("A skill with title: " + dto.title() + " already exists.");
            }
    });

        SkillStatus oldStatus = existingSkill.getStatus();
        skillMapper.updateEntityFromDTO(dto, existingSkill);

        if (oldStatus != SkillStatus.MASTERED && existingSkill.getStatus() == SkillStatus.MASTERED) {
            existingSkill.setCompletedAt(LocalDateTime.now());
        }

        else if (oldStatus == SkillStatus.MASTERED && existingSkill.getStatus() != SkillStatus.MASTERED) {
            existingSkill.setCompletedAt(null);
        }

        return skillMapper.toDTO(skillRepository.save(existingSkill));
    }

    @Transactional
    public void deleteSkill(Long id) {
        User currentUser = getCurrentUser();

        Skill existingSkill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE + id));

        if (!existingSkill.getUser().getId().equals(currentUser.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Not authorized to delete this skill");
        }

        skillRepository.delete(existingSkill);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

    }
}
