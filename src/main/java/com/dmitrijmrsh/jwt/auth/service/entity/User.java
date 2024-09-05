package com.dmitrijmrsh.jwt.auth.service.entity;

import com.dmitrijmrsh.jwt.auth.service.entity.enums.UserPrivilegeEnum;
import com.dmitrijmrsh.jwt.auth.service.entity.enums.UserRoleEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "c_first_name")
    private String firstName;

    @Column(name = "c_last_name")
    private String lastName;

    @Column(name = "c_email")
    private String email;

    @Column(name = "c_password")
    private String password;

    @Column(name = "c_role")
    @Enumerated(EnumType.STRING)
    private UserRoleEnum role;

    @Column(name = "c_privilege")
    @Enumerated(EnumType.STRING)
    private UserPrivilegeEnum privilege;
}
