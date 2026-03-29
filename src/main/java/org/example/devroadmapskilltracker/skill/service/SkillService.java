package org.example.devroadmapskilltracker.skill.service;

import org.example.devroadmapskilltracker.skill.Skill;
import org.example.devroadmapskilltracker.skill.SkillRepository;
import org.example.devroadmapskilltracker.skill.SkillStatus;
import org.example.devroadmapskilltracker.skill.dto.CreateSkillDTO;
import org.example.devroadmapskilltracker.skill.dto.SkillDTO;
import org.example.devroadmapskilltracker.skill.dto.UpdateSkillDTO;
import org.example.devroadmapskilltracker.skill.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SkillService {

    private static final String NOT_FOUND_MESSAGE = "Error: Could not find skill with id: ";

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    public SkillService(SkillRepository skillRepository, SkillMapper skillMapper) {
        this.skillRepository = skillRepository;
        this.skillMapper = skillMapper;
    }

    public SkillDTO getSkillById(Long id) {
        return skillRepository.findById(id)
                .map(skillMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE + id));

    }

    public Page<SkillDTO> getSkills(String title, String tag, Pageable pageable) {
        Page<Skill> result;

        if ((title == null || title.isBlank()) && (tag == null || tag.isBlank())) {
            result = skillRepository.findAll(pageable);

        } else if (tag == null || tag.isBlank()) {
            result = skillRepository.findByTitleContainingIgnoreCase(title, pageable);

        } else {
            result = skillRepository.findByTitleContainingIgnoreCaseOrTagIgnoreCase(title, tag, pageable);
        }

        return result.map(skillMapper::toDTO);
    }

    @Transactional
    public SkillDTO createSkill(CreateSkillDTO dto) {

        if (skillRepository.existsByTitle(dto.title())) {
            throw new IllegalArgumentException("A skill with title: " + dto.title() + " already exists.");
        }

        Skill skillEntity = skillMapper.toEntity(dto);

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

        Skill existingSkill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE + id));

        skillRepository.findByTitleIgnoreCase(dto.title()).ifPresent(foundSkill -> {
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

        Skill updatedSkill = skillRepository.save(existingSkill);

        return skillMapper.toDTO(updatedSkill);
    }

    @Transactional
    public void deleteSkill(Long id) {

        Skill existingSkill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE + id));

        skillRepository.delete(existingSkill);
    }
}
