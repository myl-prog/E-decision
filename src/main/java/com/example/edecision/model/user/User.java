package com.example.edecision.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;

@Data
@Entity(name = "user")
@Table(name = "user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(notes = "Identifiant de l'utilisateur", value = "1", required = true)
    private int id;

    @Column(name = "login")
    @ApiModelProperty(notes = "Login de connexion", value = "Toto", required = true)
    private String login;

    @Column(name = "first_name")
    @ApiModelProperty(notes = "Prénom", value = "Jacques", required = true)
    private String firstName;

    @Column(name = "last_name")
    @ApiModelProperty(notes = "Nom de famille", value = "Dupont", required = true)
    private String lastName;

    @Column(name = "password")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ApiModelProperty(notes = "Mot de passe", value = "Toto31@", required = true)
    private String password;

    @ManyToOne
    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(notes = "Rôle de l'utilisateur dans un projet", required = true)
    private UserRole userRole;

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return this.login;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

    public User(int id, String login, String firstName, String lastName, String password, UserRole userRole) {
        this.id = id;
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.userRole = userRole;
    }

    public User() {

    }
}