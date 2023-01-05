package com.example.edecision.repository.comment;

import com.example.edecision.model.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
