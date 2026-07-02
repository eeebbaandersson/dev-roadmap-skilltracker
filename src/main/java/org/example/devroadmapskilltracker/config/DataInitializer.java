package org.example.devroadmapskilltracker.config;

import org.example.devroadmapskilltracker.skill.Skill;
import org.example.devroadmapskilltracker.skill.SkillRepository;
import org.example.devroadmapskilltracker.skill.SkillStatus;
import org.example.devroadmapskilltracker.user.User;
import org.example.devroadmapskilltracker.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    @Profile("!prod")
    CommandLineRunner initDatabase(SkillRepository repository, UserRepository userRepository) {
        return args -> {

            if (repository.count() == 0) {
                logger.info("No skill found. Generating test user and skills...");

                User testUser = new User();
                testUser.setFullName("Test Developer");
                testUser.setUsername("test");
                testUser.setPassword(passwordEncoder.encode("secret"));

                User savedUser = userRepository.save(testUser);

                repository.saveAll(List.of(
                        // BACKLOG
                        new Skill("Docker", "Learn containerization and how to manage images.","DevOps", SkillStatus.BACKLOG, savedUser),
                        new Skill("TypeScript", "Strongly typed JavaScript for better scaling.","Frontend", SkillStatus.BACKLOG,savedUser),

                        // IN PROGRESS
                        new Skill("Spring Boot", "Building robust backend services with Java.","Backend", SkillStatus.IN_PROGRESS,savedUser),
                        new Skill("Thymeleaf", "Server-side template engine for modern web apps.","Web", SkillStatus.IN_PROGRESS, savedUser),
                        new Skill("CSS Grid", "Mastering complex layouts with grid systems.","Design", SkillStatus.IN_PROGRESS, savedUser),

                        // MASTERED
                        new Skill("Java Fundamentals", "Core syntax, OOP, and collections.", "Backend", SkillStatus.MASTERED, savedUser),
                        new Skill("REST APIs", "Designing and implementing scalable endpoints." ,"Backend", SkillStatus.MASTERED,savedUser)

                ));

                logger.info("🚀Test data has been loaded!");
            }
        };
    }
}
