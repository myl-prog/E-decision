package com.example.edecision.service;

import com.example.edecision.model.Comment;
import com.example.edecision.model.Proposition;
import com.example.edecision.model.User;
import com.example.edecision.repository.CommentRepository;
import com.example.edecision.repository.PropositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {
    @Autowired
    public CommentRepository commentRepository;
    public PropositionRepository propositionRepository;

    public List<Comment> getAll(){
        return commentRepository.findAll();
    }

    public Comment getComment(Integer id) {
        return commentRepository.findById(id).get();
    }
    public Comment createComment(Comment comment, Integer id) {
        comment.setProposition(id);
        return commentRepository.save(comment);
    }

    public void deleteUser(Integer id) {
        commentRepository.deleteById(id);
    }
    public List<Comment> getCommentsByPropositionID(Integer id) {
        List<Comment> comments = commentRepository.findAll();
        List<Comment> commentsByPropositionID = new ArrayList<>();
        for (Comment comment : comments) {
            if (comment.getProposition() == id) {
                commentsByPropositionID.add(comment);
            }
        }
        return commentsByPropositionID;
    }
}
