package com.example.edecision.model;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "user_proposition")
public class UserProposition {

    @Column(name = "user_id")
    public int user_id;

    @Id
    @Column(name = "proposition_id")
    public int proposition_id;
}
