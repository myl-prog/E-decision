package com.example.edecision.service;

import com.example.edecision.model.comment.Comment;
import com.example.edecision.model.comment.CommentBody;
import com.example.edecision.model.exception.CustomException;
import com.example.edecision.model.proposition.Proposition;
import com.example.edecision.model.user.User;
import com.example.edecision.repository.comment.CommentRepository;
import com.example.edecision.service.PropositionService;
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

    // ===============
    // === Comment ===
    // ===============

    /**
     * Permet de récupérer tous les commentaires d'une proposiiton
     *
     * @param projectId     id du projet
     * @param propositionId id de la proposition
     * @return la liste des commentaires
     */
    public List<Comment> getProjectPropositionCommentsById(int projectId, int propositionId) {
        // Génère une exception si elle n'existe  pas
        propositionService.getProjectPropositionById(projectId, propositionId);

        // Retourne l'ensemble des commentaires pour la proposition
        return commentRepo.getPropositionComments(projectId, propositionId);
    }

    /**
     * Permet d'ajouter un commentaire sur une proposition
     *
     * @param projectId     id du projet
     * @param propositionId id de la proposition
     * @param body          objet du commentaire
     * @return le commentaire ajouté
     */
    public Comment createProjectPropositionComment(int projectId, int propositionId, CommentBody body) {
        // Récupère l'utilisateur qui veut ajouter le commentaire
        User currentUser = Common.GetCurrentUser();

        // Génère une exception si elle n'existe  pas
        propositionService.getProjectPropositionById(projectId, propositionId);

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

    /**
     * Permet de modifier un commentaire par son créateur
     *
     * @param projectId     id du projet
     * @param propositionId id de la proposition
     * @param commentId     id du commentaire
     * @param body          objet du commentaire
     * @return le commentaire modifé
     */
    public Comment updateProjectPropositionComment(int projectId, int propositionId, int commentId, CommentBody body) {
        // Récupère l'utilisateur qui veut ajouter le commentaire
        User currentUser = Common.GetCurrentUser();

        // Récupère le commentaire
        Optional<Comment> optionalComment = commentRepo.getPropositionCommentById(projectId, propositionId, commentId);

        if (optionalComment.isEmpty())
            throw new CustomException("This proposition comment doesn't exists", HttpStatus.NOT_FOUND);

        Comment comment = optionalComment.get();

        // Vérifie que l'utilisateur courant soit l'auteur du commentaire
        if (comment.getUser().getId() != currentUser.getId())
            throw new CustomException("You are not the author of this comment", HttpStatus.FORBIDDEN);

        // Met à jour le commentaire
        comment.setTitle(body.getTitle());
        comment.setContent(body.getContent());
        comment.setLastChangeDate(Common.GetCurrentLocalDate());

        return commentRepo.save(comment);
    }

    /**
     * Permet de supprimer un commentaire par son créateur
     *
     * @param projectId     id du projet
     * @param propositionId id de la proposition
     * @param commentId     id du commentaire
     */
    public void deleteProjectPropositionCommentById(int projectId, int propositionId, int commentId) {
        // Récupère l'utilisateur qui veut ajouter le commentaire
        User currentUser = Common.GetCurrentUser();

        // Récupère le commentaire
        Optional<Comment> optionalComment = commentRepo.getPropositionCommentById(projectId, propositionId, commentId);

        if (optionalComment.isEmpty())
            throw new CustomException("This proposition comment doesn't exists", HttpStatus.NOT_FOUND);

        Comment comment = optionalComment.get();

        // Vérifie que l'utilisateur courant soit l'auteur du commentaire
        if (comment.getUser().getId() != currentUser.getId())
            throw new CustomException("You are not the author of this comment", HttpStatus.FORBIDDEN);

        // Supprime le commentaire
        commentRepo.deletePropositionCommentById(commentId);
    }

}
