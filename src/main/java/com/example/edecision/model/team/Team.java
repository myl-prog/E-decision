package com.example.edecision.model.team;

import com.example.edecision.model.proposition.PropositionStatus;
import com.example.edecision.model.user.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "team")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "project_id")
    private int projectId;

    @ManyToOne()
    @JoinColumn(name = "team_type_id", referencedColumnName = "id")
    private TeamType teamType;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User owner;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @OneToMany
    private List<User> users;
}
