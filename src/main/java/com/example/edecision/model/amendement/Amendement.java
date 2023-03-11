package com.example.edecision.model.amendement;

import com.example.edecision.model.proposition.Proposition;
import com.example.edecision.model.proposition.PropositionStatus;
import com.example.edecision.model.user.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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