package com.example.edecision.model.vote;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity(name = "vote_type")
@Table(name = "vote_type")
public class VoteType {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

}
