package org.example.devroadmapskilltracker.skill;

import org.example.devroadmapskilltracker.skill.dto.CreateSkillDTO;
import org.example.devroadmapskilltracker.skill.dto.SkillDTO;
import org.example.devroadmapskilltracker.skill.dto.UpdateSkillDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SkillService {

    private static  final String NOT_FOUND_MESSAGE = "Error: Could not find skill with id: ";

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    public SkillService(SkillRepository skillRepository, SkillMapper skillMapper) {
        this.skillRepository = skillRepository;
        this.skillMapper = skillMapper;
    }

    public Page<SkillDTO> getAllSkills(Pageable pageable) {
        return skillRepository.findAll(pageable)
                .map(skillMapper::toDTO);
    }

    public SkillDTO getSkillById(Long id) {
        return skillRepository.findById(id)
                .map(skillMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE + id));

    }
    
    public SkillDTO createSkill(CreateSkillDTO dto) {

        // Kontroll --> Finns titeln redan?
        if (skillRepository.existsByTitle(dto.title())) {
            throw new IllegalArgumentException("A skill with title: " + dto.title() + " already exists.");
        }

        if (dto.dateAdded().isAfter(LocalDate.now())){
            throw new IllegalArgumentException("Date cannot be in the future.");
        }

        // Använder mappern för att skapa entitet från DTO:n
        Skill skillEntity = skillMapper.toEntity(dto);

        // Sparar entiteten i databasen genom repositoryt
        // Repositoryt returnerar den sparade entitetn (nu med ett genererart ID)
        Skill savedSkill = skillRepository.save(skillEntity);

        // Mappar tillbaka den sparade entiteten till en SkillDTO och returnera
        return skillMapper.toDTO(savedSkill);
    }


    public SkillDTO updateSkill(Long id, UpdateSkillDTO dto) {

        // Hämta befintlig skill eller kasta exception om den saknas
        Skill existingSkill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE + dto.id()));

       // Uppdatera fältet på den befintliga entiteten med data från DTO:n
        skillMapper.updateEntityFromDTO(dto, existingSkill);

        existingSkill.setId(id);

        // Sparar ändringarna
        Skill updatedSkill = skillRepository.save(existingSkill);

        // Returnera den uppdaterade versionen som DTO
        return skillMapper.toDTO(updatedSkill);
    }

    public void deleteSkill(Long id) {

        Skill existingSkill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE + id));

        skillRepository.delete(existingSkill);
    }
}
