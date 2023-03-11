package com.example.edecision.model.comment;

import com.example.edecision.model.user.User;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity(name = "comment")
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "date")
    private Date description;

    @Column(name = "is_escalated")
    private Boolean isEscalated;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "proposition_id")
    private int proposition_id;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public Comment(){}
    public Comment(int proposition_id, boolean isEscalated, boolean isDeleted, User user){
        this.proposition_id = proposition_id;
        this.isDeleted = isDeleted;
        this.isEscalated = isEscalated;
        this.user = user;
    }
}
