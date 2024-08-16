package com.dmitrijmrsh.jwt.auth.service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(min = 4, max = 255, message = "Username size should be from 4 to 255")
    @Column(name = "c_username", nullable = false, unique = true)
    private String username;

    @Column(name = "c_password", nullable = false)
    @Size(min = 8, message = "Password size should be at least 8 characters")
    private String password;

    @Column(name = "c_email", nullable = false, unique = true)
    private String email;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "t_user_authority",
               joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id"))
    private List<Authority> authorities;
}
