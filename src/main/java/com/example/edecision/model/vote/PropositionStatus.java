package com.example.edecision.model.vote;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "proposition_status")
@Table(name = "proposition_status")
public class PropositionStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;
}