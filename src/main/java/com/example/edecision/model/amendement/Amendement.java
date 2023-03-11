package com.example.edecision.model.amendement;

import com.example.edecision.model.proposition.Proposition;
import com.example.edecision.model.user.User;
import com.example.edecision.model.vote.PropositionStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "amendement")
@Table(name = "amendement")
public class Amendement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @ManyToOne()
    @JoinColumn(name = "amendement_status", referencedColumnName = "id")
    private PropositionStatus amendement_status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "amend_proposition_id", referencedColumnName = "id")
    private Proposition amend_proposition;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isEditable;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isVoteable;
}