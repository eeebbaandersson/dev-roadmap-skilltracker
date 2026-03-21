package org.example.devroadmapskilltracker.skill.service;

import org.example.devroadmapskilltracker.skill.Skill;
import org.example.devroadmapskilltracker.skill.dto.CreateSkillDTO;
import org.example.devroadmapskilltracker.skill.dto.SkillDTO;
import org.example.devroadmapskilltracker.skill.dto.UpdateSkillDTO;
import org.springframework.stereotype.Component;

@Component // För att Spring ska kunna använda inject av klassen i Servicen
public class SkillMapper {

    // CreateSkillDTO --> Entity
    public Skill toEntity(CreateSkillDTO dto) {
        Skill skill = new Skill();
        skill.setTitle(dto.title());
        skill.setStatus(dto.status());
        skill.setDescription(dto.description());
        skill.setSource(dto.source());
        skill.setTag(dto.tag());
        return skill;
    }

    // Entity --> SkillDTO (för visning)
    public SkillDTO toDTO(Skill skill) {
        return new SkillDTO(
                skill.getId(),
                skill.getTitle(),
                skill.getStatus(),
                skill.getDescription(),
                skill.getSource(),
                skill.getDateAdded(),
                skill.getUpdatedAt(),
                skill.getCompletedAt(),
                skill.getTag()
        );
    }

    // UpdateSkillDTO --> Uppdatera befintlig Entity
    public void updateEntityFromDTO(UpdateSkillDTO dto, Skill skill) {
        skill.setTitle(dto.title());
        skill.setStatus(dto.status());
        skill.setDescription(dto.description());
        skill.setSource(dto.source());
        skill.setTag(dto.tag());
    }

}
