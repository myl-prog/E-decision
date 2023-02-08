package com.example.edecision.model;
import lombok.Data;
import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "user_team")
public class UserTeam {

    @Column(name = "user_id")
    private int user_id;
    @Column(name = "team_id")
    private int team_id;

    public UserTeam() {

    }

}
