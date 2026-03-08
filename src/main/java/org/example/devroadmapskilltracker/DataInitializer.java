package org.example.devroadmapskilltracker;

import org.example.devroadmapskilltracker.skill.SkillRepository;
import org.example.devroadmapskilltracker.skill.Skill;
import org.example.devroadmapskilltracker.skill.SkillStatus;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(SkillRepository repository) {
        return args -> {
            // Vi rensar gamla data först så vi inte får dubbletter vid omstart
            repository.deleteAll();

            repository.saveAll(List.of(
                    // BACKLOG
                    new Skill("Docker", "Learn containerization and how to manage images.", LocalDate.now(), "DevOps", SkillStatus.BACKLOG),
                    new Skill("TypeScript", "Strongly typed JavaScript for better scaling.", LocalDate.now(), "Frontend", SkillStatus.BACKLOG),

                    // IN PROGRESS
                    new Skill("Spring Boot", "Building robust backend services with Java.", LocalDate.now(), "Backend", SkillStatus.IN_PROGRESS),
                    new Skill("Thymeleaf", "Server-side template engine for modern web apps.", LocalDate.now(), "Web", SkillStatus.IN_PROGRESS),
                    new Skill("CSS Grid", "Mastering complex layouts with grid systems.", LocalDate.now(), "Design", SkillStatus.IN_PROGRESS),

                    // MASTERED
                    new Skill("Java Fundamentals", "Core syntax, OOP, and collections.", LocalDate.now(), "Backend", SkillStatus.MASTERED),
                    new Skill("REST APIs", "Designing and implementing scalable endpoints.", LocalDate.now(), "Backend", SkillStatus.MASTERED)
            ));

            System.out.println("Testdata har laddats in!");
        };
    }
}
