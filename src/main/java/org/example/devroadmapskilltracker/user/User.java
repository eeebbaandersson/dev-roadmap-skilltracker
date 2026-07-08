package org.example.devroadmapskilltracker.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "A name is required") private String fullName;

    @NotBlank(message = "A username is required") @Column(unique = true, nullable = false)
    private String username;
    @NotBlank(message = "A password is required") private String password;
}
