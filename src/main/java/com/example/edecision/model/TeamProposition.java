package com.example.edecision.model;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "team_proposition")
public class TeamProposition {

    @Column(name = "team_id")
    public int team_id;

    @Id
    @Column(name = "proposition_id")
    public int proposition_id;
}
