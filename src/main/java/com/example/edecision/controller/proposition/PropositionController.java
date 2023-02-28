package com.example.edecision.controller.proposition;

import com.example.edecision.model.comment.Comment;
import com.example.edecision.model.proposition.AmendPropositionBody;
import com.example.edecision.model.proposition.Proposition;
import com.example.edecision.model.proposition.PropositionBody;
import com.example.edecision.service.comment.CommentService;
import com.example.edecision.service.proposition.PropositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PropositionController {
    @Autowired
    public PropositionService propositionService;

    @Autowired
    public CommentService commentService;

    @GetMapping("/propositions")
    public ResponseEntity<List<Proposition>> getAll() {
        try {
            return new ResponseEntity<>(propositionService.getAllPropositionByUser(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/propositions/{id}")
    public ResponseEntity<HttpStatus> deletePropositionById(@PathVariable("id") int id) {
        try {
            propositionService.deleteProposition(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ========================
    // ======= COMMENTS =======
    // ========================
//Get all comments
    @GetMapping("/propositions/comments")
    public ResponseEntity<List<Comment>> getAllComments(@PathVariable("id") Integer id) {
        try {
            return new ResponseEntity<>(commentService.getAll(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Get all comments of a proposition
    @GetMapping("/propositions/{id}/comments")
    public ResponseEntity<List<Comment>> getCommentByPropositionId(@PathVariable("id") Integer id) {
        try {
            return new ResponseEntity<>(commentService.getCommentsByPropositionID(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Get comment by id of comment
    @GetMapping("/propositions/{proposition_id}/comments/{comment_id}")
    public ResponseEntity<Comment> getById(@PathVariable("id") int id) {
        try {
            return new ResponseEntity<>(commentService.getComment(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Creation of comment
    @PostMapping("/propositions/{id}/comments")
    public ResponseEntity<Comment> postComment(@RequestBody Comment comment, @PathVariable("id") int id) {
        try {
            return new ResponseEntity<>(commentService.createComment(comment, id), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Delete comment
    @DeleteMapping("/propositions/comments/{id}")
    public ResponseEntity<HttpStatus> deleteById(@PathVariable("id") int id) {
        try {
            commentService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // =====================
    // ======= VOTES =======
    // =====================


}
