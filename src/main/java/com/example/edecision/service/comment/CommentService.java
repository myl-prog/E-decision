package com.example.edecision.service.comment;

import com.example.edecision.model.comment.Comment;
import com.example.edecision.model.comment.CommentBody;
import com.example.edecision.repository.comment.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {
    @Autowired
    public CommentRepository commentRepo;

    public List<Comment> getProjectPropositionCommentsById(int projectId, int propositionId){
        return new ArrayList<Comment>();
    }

    public Comment createProjectPropositionComment(int projectId, int propositionId, CommentBody body){
        return new Comment(propositionId, body.getTitle(), body.getContent());
    }

    public Comment updateProjectPropositionComment(int projectId, int propositionId, int commentId, CommentBody body){
        return new Comment(propositionId, body.getTitle(), body.getContent());
    }

    public void deleteProjectPropositionCommentById(int projectId, int propositionId, int commentId){
    }

}
