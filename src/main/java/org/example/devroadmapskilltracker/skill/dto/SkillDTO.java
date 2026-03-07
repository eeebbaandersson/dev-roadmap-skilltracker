package org.example.devroadmapskilltracker.skill.dto;

import org.example.devroadmapskilltracker.skill.SkillStatus;

import java.time.LocalDate;

public record SkillDTO(
        Long id,
        String title,
        SkillStatus status,
        String description,
        String source,
        LocalDate dateAdded,
        String tag
) {}
