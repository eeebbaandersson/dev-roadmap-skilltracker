package org.example.devroadmapskilltracker.skill;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    @Profile("!prod")
    CommandLineRunner initDatabase(SkillRepository repository) {
        return args -> {

            if (repository.count() == 0) {

                repository.saveAll(List.of(
                        // BACKLOG
                        new Skill("Docker", "Learn containerization and how to manage images.","DevOps", SkillStatus.BACKLOG),
                        new Skill("TypeScript", "Strongly typed JavaScript for better scaling.","Frontend", SkillStatus.BACKLOG),

                        // IN PROGRESS
                        new Skill("Spring Boot", "Building robust backend services with Java.","Backend", SkillStatus.IN_PROGRESS),
                        new Skill("Thymeleaf", "Server-side template engine for modern web apps.","Web", SkillStatus.IN_PROGRESS),
                        new Skill("CSS Grid", "Mastering complex layouts with grid systems.","Design", SkillStatus.IN_PROGRESS),

                        // MASTERED
                        new Skill("Java Fundamentals", "Core syntax, OOP, and collections.", "Backend", SkillStatus.MASTERED),
                        new Skill("REST APIs", "Designing and implementing scalable endpoints." ,"Backend", SkillStatus.MASTERED)

                ));

                logger.info("🚀Test data has been loaded!");
            }
        };
    }
}
