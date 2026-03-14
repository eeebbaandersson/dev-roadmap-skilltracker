package org.example.devroadmapskilltracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DevRoadmapSkilltrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevRoadmapSkilltrackerApplication.class, args);
    }

}
