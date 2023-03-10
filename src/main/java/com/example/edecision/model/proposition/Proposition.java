package com.example.edecision.model.proposition;

import com.example.edecision.model.project.Project;
import com.example.edecision.model.team.Team;
import com.example.edecision.model.user.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity(name = "proposition")
@Table(name = "proposition")
public class Proposition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "begin_time")
    private Date begin_time;

    @Column(name = "end_time")
    private Date end_time;

    @Column(name = "amendment_delay")
    private int amendment_delay;

    @Column(name = "content")
    private String content;

    @ManyToOne()
    @JoinColumn(name = "proposition_status_id", referencedColumnName = "id")
    private PropositionStatus proposition_status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "amend_proposition_id", referencedColumnName = "id")
    private Proposition amend_proposition;

    @ManyToOne()
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private Project project;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @OneToMany
    private List<User> users;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @OneToMany
    private List<Team> teams;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isEditable;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isVoteable;
}