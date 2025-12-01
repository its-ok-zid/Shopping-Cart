package com.cts.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // username: plain string (no email constraint)
    @NotBlank
    @Column(name = "username", unique = true, nullable = false, length = 100)
    private String username;

    // password: DON'T enforce unique
    @NotBlank
    @JsonIgnore
    @Column(name = "password", nullable = false, length = 255)   // bcrypt needs ~60 chars
    private String password;

    // email: validated
    @Email
    @Column(name = "email", unique = true, length = 254)
    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles;
}
