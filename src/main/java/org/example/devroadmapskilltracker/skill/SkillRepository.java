package org.example.devroadmapskilltracker.skill;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    @Modifying
    @Transactional
    void deleteByUserId(Long userId);

    Page<Skill> findAllByUserId(Long userid,Pageable pageable);

    boolean existsByTitleAndUserId(String title, Long userId);

    Page<Skill> findByTitleContainingIgnoreCaseAndUserId(String title, Long userId, Pageable pageable);

    Page<Skill> findByTitleContainingIgnoreCaseOrTagIgnoreCaseAndUserId(String title, String tag, Long userId, Pageable pageable);

    Optional<Skill> findByTitleIgnoreCaseAndUserId(String title, Long userId);



}
