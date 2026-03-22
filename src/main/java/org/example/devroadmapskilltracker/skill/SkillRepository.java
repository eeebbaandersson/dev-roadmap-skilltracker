package org.example.devroadmapskilltracker.skill;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    // Standardmetoden --> Finns i JpaRepository, deklarerad för tydlighet
    Page<Skill> findAll(Pageable pageable);

    boolean existsByTitle(String title);

    // Kombinerad sökning --> Title + Tag
    Page<Skill> findByTitleContainingIgnoreCaseOrTagIgnoreCase(String title, String tag,Pageable pageable);

    // Filtrera på Status
    Page<Skill> findByStatus(SkillStatus status, Pageable pageable);

    // Specifik tagg-sökning -> För framtida behov?
    Page<Skill> findByTagContainingIgnoreCase(String tag,Pageable pageable);

    Page<Skill> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Optional<Skill> findByTitleIgnoreCase(String title);

}

// AI Review Trigger
