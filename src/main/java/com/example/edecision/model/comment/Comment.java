package com.example.edecision.model.comment;

import com.example.edecision.model.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(name = "creation_date")
    private Date creationDate;

    @Column(name = "last_change_date")
    private Date lastChangeDate;

    @Column(name = "is_escalated")
    private Boolean isEscalated;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @JsonIgnore
    @Column(name = "proposition_id")
    private int propositionId;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public Comment(){}
    public Comment(int propositionId, String title, String content){
        this.propositionId = propositionId;
        this.title = title;
        this.content = content;
    }
    public Comment(int propositionId, boolean isEscalated, boolean isDeleted, User user){
        this.propositionId = propositionId;
        this.isDeleted = isDeleted;
        this.isEscalated = isEscalated;
        this.user = user;
    }
}
