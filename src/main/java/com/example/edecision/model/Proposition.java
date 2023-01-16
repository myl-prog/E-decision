package com.example.edecision.model;
import com.example.edecision.model.User.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import javax.persistence.*;
import java.util.Date;

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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ManyToOne
    private User[] users;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ManyToOne
    private Team[] teams;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isEditable;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isVoteable;
}