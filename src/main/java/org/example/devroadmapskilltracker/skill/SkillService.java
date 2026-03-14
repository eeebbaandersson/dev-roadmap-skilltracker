package org.example.devroadmapskilltracker.skill;

import org.example.devroadmapskilltracker.skill.dto.CreateSkillDTO;
import org.example.devroadmapskilltracker.skill.dto.SkillDTO;
import org.example.devroadmapskilltracker.skill.dto.UpdateSkillDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
// @Transactional(readOnly = true)

public class SkillService {

    private static  final String NOT_FOUND_MESSAGE = "Error: Could not find skill with id: ";

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

        // Om både title och tag saknas, hämta allt
        if ((title == null || title.isBlank()) && (tag == null || tag.isBlank())) {
            result = skillRepository.findAll(pageable);

            // Finns title men tag saknas --> Utför titel-sökning
        } else if (tag == null || tag.isBlank()) {
            result = skillRepository.findByTitleContainingIgnoreCase(title, pageable);

        } else {
            result = skillRepository.findByTitleContainingIgnoreCaseOrTagIgnoreCase(title, tag, pageable);
        }

        // Mappa allt till DTO och returnera
        return result.map(skillMapper::toDTO);
    }

    public SkillDTO createSkill(CreateSkillDTO dto) {

        // Kontroll --> Finns titeln redan?
        if (skillRepository.existsByTitle(dto.title())) {
            throw new IllegalArgumentException("A skill with title: " + dto.title() + " already exists.");
        }

        if (dto.dateAdded().isAfter(LocalDateTime.now())){
            throw new IllegalArgumentException("Date cannot be in the future.");
        }

        // Använder mappern för att skapa entitet från DTO:n
        Skill skillEntity = skillMapper.toEntity(dto);

        // Om man skapar en skill som redan är mastered, sätt completedAd direkt
        if (skillEntity.getStatus() == SkillStatus.MASTERED) {
            skillEntity.setCompletedAt(LocalDateTime.now());
        }

        // Sparar entiteten i databasen genom repositoryt
        // Repositoryt returnerar den sparade entitetn (nu med ett genererart ID)
        Skill savedSkill = skillRepository.save(skillEntity);

        // Mappar tillbaka den sparade entiteten till en SkillDTO och returnera
        return skillMapper.toDTO(savedSkill);
    }


    @Transactional
    public SkillDTO updateSkill(Long id, UpdateSkillDTO dto) {

        // Hämta befintlig skill eller kasta exception om den saknas
        Skill existingSkill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MESSAGE + id));

        // Sparar undan den gamla statusen
        SkillStatus oldStatus = existingSkill.getStatus();

       // Uppdatera fältet på den befintliga entiteten med data från DTO:n
        skillMapper.updateEntityFromDTO(dto, existingSkill);

        // Om statusen sätts till Mastered --> sätt dagens datum
        if (oldStatus != SkillStatus.MASTERED && existingSkill.getStatus() == SkillStatus.MASTERED) {
            existingSkill.setCompletedAt(LocalDateTime.now());
        }
        // Om statusen ändras från Mastered till något annat --> Nollställ datumet
        else if (oldStatus == SkillStatus.MASTERED && existingSkill.getStatus() != SkillStatus.MASTERED) {
            existingSkill.setCompletedAt(null);
        }

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
