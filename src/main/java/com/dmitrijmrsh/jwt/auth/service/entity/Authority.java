package com.dmitrijmrsh.jwt.auth.service.entity;

import com.dmitrijmrsh.jwt.auth.service.entity.enums.UserAuthorityEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_authority")
public class Authority implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "c_authority", nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private UserAuthorityEnum authority;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "t_user_authority",
               joinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private List<User> users;

    @Override
    public String getAuthority() {
        return authority.getAuthorityInString();
    }
}
