package pl.kucharski.Kordi.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pl.kucharski.Kordi.config.PaginationConstants;
import pl.kucharski.Kordi.exception.CollectionNotFoundException;
import pl.kucharski.Kordi.exception.CommentNotFoundException;
import pl.kucharski.Kordi.exception.UserNotFoundException;
import pl.kucharski.Kordi.model.comment.CommentDTO;
import pl.kucharski.Kordi.model.comment.CreateCommentDTO;
import pl.kucharski.Kordi.service.collection.CommentService;

import java.net.URI;
import java.util.Collections;
import java.util.List;

/**
 * Comment controller responsible for comment management
 *
 * @author Grzegorz Kucharski gelo2424@wp.pl
 */
@Slf4j
@RestController
@RequestMapping("/collections")
public class CommentController {

    CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Add new comment
     * @param collectionId id of collection which you want to add comment
     * @param comment comment to add
     *
     * @return created comment<br>
     * status 404 if collection not found
     */
    @PostMapping("/{collectionId}/comments")
    ResponseEntity<?> addComment(@PathVariable("collectionId") Long collectionId, @RequestBody CreateCommentDTO comment){
        try {
            log.info("Request to add comment: {} to collection with id {}", comment, collectionId);
            CommentDTO addedComment = commentService.addComment(collectionId, comment);
            return ResponseEntity.created(
                    URI.create("/collections/" + collectionId + "/comments/" + addedComment.getId())
            ).body(addedComment);
        } catch (CollectionNotFoundException | UserNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    /**
     * Remove comment from collection
     * @param collectionId id of collection from which you want to remove comment
     * @param commentId id of comment to delete
     *
     * @return info about deleted comment
     * status 404 if collection not found
     * status 404 if comment not found in given collection
     */
    @DeleteMapping("/{collectionId}/comments/{commentId}")
    ResponseEntity<?> removeComment(@PathVariable("collectionId") Long collectionId, @PathVariable("commentId") Long commentId){
        try {
            log.info("Request to remove comment with id {} from collection with id {}", commentId, collectionId);
            commentService.removeComment(collectionId, commentId);
            return ResponseEntity.ok(Collections.singletonMap("status", "Comment with id " + commentId + " deleted"));
        } catch (CollectionNotFoundException | CommentNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    /**
     * Get all comment from collection
     * @param collectionId id of collection from which you want to remove comment
     *
     * @return list of found comments
     * status 404 if collection not found
     */
    @GetMapping("/{collectionId}/comments")
    ResponseEntity<?> getCommentFromCollection(@PathVariable("collectionId") Long collectionId,
                                               @RequestParam(value = "pageNo",
                                                       defaultValue = PaginationConstants.DEFAULT_PAGE_NUMBER,
                                                       required = false) int pageNo,
                                               @RequestParam(value = "pageSize",
                                                       defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE,
                                                       required = false) int pageSize){
        try {
            log.info("Request to get all comments from collection with id {}", collectionId);
            Page<CommentDTO> comments = commentService
                    .getAllComments(collectionId, PageRequest.of(pageNo, pageSize, Sort.by("createdTime").descending()));
            return ResponseEntity.ok(comments);
        } catch (CollectionNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

}
