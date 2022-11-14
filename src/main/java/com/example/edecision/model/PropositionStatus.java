package com.example.edecision.model;
import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "proposition_status")
public class PropositionStatus {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;
}