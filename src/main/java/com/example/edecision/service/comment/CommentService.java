package com.example.edecision.service.comment;

import com.example.edecision.model.comment.Comment;
import com.example.edecision.model.comment.CommentBody;
import com.example.edecision.model.exception.CustomException;
import com.example.edecision.model.proposition.Proposition;
import com.example.edecision.model.user.User;
import com.example.edecision.repository.comment.CommentRepository;
import com.example.edecision.service.proposition.PropositionService;
import com.example.edecision.utils.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    public CommentRepository commentRepo;

    @Autowired
    public PropositionService propositionService;

    public List<Comment> getProjectPropositionCommentsById(int projectId, int propositionId)
    {
        // Récupère la proposition et génère une exception si elle n'existe  pas
        Proposition proposition = propositionService.getProjectPropositionById(projectId, propositionId);

        // Retourne l'ensemble des commentaires pour la proposition
        return commentRepo.getPropositionComments(projectId, propositionId);
    }

    public Comment createProjectPropositionComment(int projectId, int propositionId, CommentBody body)
    {
        // Récupère l'utilisateur qui veut ajouter le commentaire
        User currentUser = Common.GetCurrentUser();

        // Récupère la proposition et génère une exception si elle n'existe  pas
        Proposition proposition = propositionService.getProjectPropositionById(projectId, propositionId);

        // Remplis toutes les propriétés de l'objet
        Comment comment = new Comment();
        comment.setTitle(body.getTitle());
        comment.setContent(body.getContent());
        comment.setCreationDate(Common.GetCurrentLocalDate());
        comment.setLastChangeDate(Common.GetCurrentLocalDate());
        comment.setPropositionId(propositionId);
        comment.setIsDeleted(false);
        comment.setIsEscalated(false);
        comment.setUser(currentUser);

        // Insère et retourne le commentaire
        return commentRepo.save(comment);
    }

    public Comment updateProjectPropositionComment(int projectId, int propositionId, int commentId, CommentBody body)
    {
        // Récupère l'utilisateur qui veut ajouter le commentaire
        User currentUser = Common.GetCurrentUser();

        // Récupère le commentaire
        Optional<Comment> optionalComment = commentRepo.getPropositionCommentById(projectId, propositionId, commentId);

        if(!optionalComment.isPresent())
            throw new CustomException("This proposition comment doesn't exists", HttpStatus.BAD_REQUEST);

        Comment comment = optionalComment.get();

        // Vérifie que l'utilisateur courant soit l'auteur du commentaire
        if(comment.getUser().getId() != currentUser.getId())
            throw new CustomException("You are not the author of this comment", HttpStatus.BAD_REQUEST);

        // Met à jour le commentaire
        comment.setTitle(body.getTitle());
        comment.setContent(body.getContent());
        comment.setLastChangeDate(Common.GetCurrentLocalDate());

        return commentRepo.save(comment);
    }

    public void deleteProjectPropositionCommentById(int projectId, int propositionId, int commentId)
    {
        // Récupère l'utilisateur qui veut ajouter le commentaire
        User currentUser = Common.GetCurrentUser();

        // Récupère le commentaire
        Optional<Comment> optionalComment = commentRepo.getPropositionCommentById(projectId, propositionId, commentId);

        if(!optionalComment.isPresent())
            throw new CustomException("This proposition comment doesn't exists", HttpStatus.BAD_REQUEST);

        Comment comment = optionalComment.get();

        // Vérifie que l'utilisateur courant soit l'auteur du commentaire
        if(comment.getUser().getId() != currentUser.getId())
            throw new CustomException("You are not the author of this comment", HttpStatus.BAD_REQUEST);

        // Supprime le commentaire
        commentRepo.deletePropositionCommentById(commentId);
    }

}
