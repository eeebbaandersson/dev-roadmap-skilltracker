package org.example.devroadmapskilltracker.skill;

import org.example.devroadmapskilltracker.skill.dto.CreateSkillDTO;
import org.example.devroadmapskilltracker.skill.dto.SkillDTO;
import org.example.devroadmapskilltracker.skill.dto.UpdateSkillDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional // Gör en rollback av ändringar i databasen efter varje test
class SkillServiceIntegrationTest {

    @Autowired
    private SkillService skillService;
    @Autowired
    private SkillRepository skillRepository;

    // Rensar all data från DataInitializer innan testen börjar --> Löser tidigare problem med för många träffar i Title/tag-search
    @BeforeEach
    void setUp() {
        skillRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveSkill() {
        // Arrange
        CreateSkillDTO dto = new CreateSkillDTO(
                "Integration Test Skill",
                SkillStatus.BACKLOG,
                "Testing DB connection",
                "https://docs.spring.io",
                LocalDateTime.now(),
                "Test"
        );

        // Act
        SkillDTO saved = skillService.createSkill(dto);

        // Assert
        assertThat(saved).isNotNull();
        assertThat(saved.title()).isEqualTo("Integration Test Skill");
    }

    @Test
    void shouldThrowException_WhenTitleAlreadyExists() {
        // Arrange
        CreateSkillDTO dto = new CreateSkillDTO(
                "Duplicate Title",
                SkillStatus.BACKLOG,
                "...",
                "https://test.io",
                LocalDateTime.now(),
                "Test"
        );
        skillService.createSkill(dto);

        // Act & Assert
        assertThatThrownBy(() -> skillService.createSkill(dto))
                .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("A skill with title: " +dto.title() + " already exists");
    }

    @Test
    void shouldReturnFilteredSkills_WhenSearchingByTitleAndTag() {
        // Arrange - Skapa flera skills
        skillService.createSkill(new CreateSkillDTO("Java Spring", SkillStatus.BACKLOG, "...", "https://test.io", LocalDateTime.now(), "Backend"));
        skillService.createSkill(new CreateSkillDTO("React Frontend", SkillStatus.BACKLOG, "...", "https://test.io", LocalDateTime.now(), "Frontend"));
        skillService.createSkill(new CreateSkillDTO("Java Hibernate", SkillStatus.BACKLOG, "...", "https://test.io", LocalDateTime.now(), "Database"));

        // Act 1: Sök på bara titel (Java)
        Page<SkillDTO> titleSearch = skillService.getSkills("Java", null, Pageable.unpaged());

        // Act 2: Sök på bara tagg (Frontend)
        Page<SkillDTO> tagSearch = skillService.getSkills(null, "Frontend", Pageable.unpaged());

        // Act 3: Sök på något som inte finns (Phyton)
        Page<SkillDTO> titleSearch2 = skillService.getSkills("Python", null, Pageable.unpaged());

        // Assert
        assertThat(titleSearch.getContent()).hasSize(2);
        assertThat(tagSearch.getContent()).hasSize(1);
        assertThat(titleSearch2.getContent()).isEmpty();


    }

    @Test
    void shouldThrowException_WhenDateIsInTheFuture() {
        // Arrange
        CreateSkillDTO futureDto = new CreateSkillDTO(
                "Future Skill",
                SkillStatus.BACKLOG,
                "...",
                "https://test.io",
                LocalDateTime.now().plusDays(1),
                "Tag"
        );

        // Act & Assert
        assertThatThrownBy(() -> skillService.createSkill(futureDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Date cannot be in the future.");

    }

    @Test
    void shouldUpdateExistingSkill() {
        // Arrange -> Skapa/spara en initial skill
        CreateSkillDTO createDto = new CreateSkillDTO(
                "Original Title",
                SkillStatus.BACKLOG,
                "Original description",
                "https://test.io",
                LocalDateTime.now(),
                "Original Tag"
        );

        SkillDTO savedInitial = skillService.createSkill(createDto);
        Long id = savedInitial.id();

        // Skapa en UpdateSkillDTO med ny data
        UpdateSkillDTO updateDto = new UpdateSkillDTO(
                id,
                "Updated Title",
                SkillStatus.IN_PROGRESS,
                "Updated description",
                "https://test.io",
                LocalDateTime.now(),
                "Updated Tag"
        );

        // Anropa servicen för att uppdatera
        SkillDTO updatedResult = skillService.updateSkill(id, updateDto);

        // Assert
        assertThat(updatedResult.id()).isEqualTo(id);
        assertThat(updatedResult.title()).isEqualTo("Updated Title");
        assertThat(updatedResult.status()).isEqualTo(SkillStatus.IN_PROGRESS);
        assertThat(updatedResult.description()).isEqualTo("Updated description");

        // Hämta från databsen igen för att kontrollerade att det sparades korrekt
        SkillDTO fetchedFromDB = skillService.getSkillById(id);
        assertThat(fetchedFromDB.title()).isEqualTo("Updated Title");
    }

    @Test
    void shouldThrowException_WhenUpdatingNonExistingSkill() {
        // Arrange
        Long nonExistingId = 99L;
        UpdateSkillDTO updateDto = new UpdateSkillDTO(
                nonExistingId, "Title",
                SkillStatus.BACKLOG,
                "Description",
                "https://test.io",
                LocalDateTime.now(),
                "Tag"
        );

        // Act & Assert
        assertThatThrownBy(() -> skillService.updateSkill(nonExistingId, updateDto))
        .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Error: Could not find skill with id: " + nonExistingId);

    }

    @Test
    void shouldDeleteChosenSkill() {
        // Arrange -
        CreateSkillDTO dto = new CreateSkillDTO(
                "Java Docs",
                SkillStatus.BACKLOG,
                "Improving ability to create Java Docs",
                "https://docs.oracle.com",
                LocalDateTime.now(),
                "Java"
        );

        SkillDTO saved = skillService.createSkill(dto);
        Long id = saved.id();

        // Act
       skillService.deleteSkill(id);

       // Assert
        assertThatThrownBy(() -> skillService.getSkillById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Error: Could not find skill with id: " + id);
    }

    // Testfall att addera:
    // Verifiera logik för completedAt
    // Verifiera updatedAT (SPRING BOOTSs JPA Auditing)


}
