package pl.kucharski.Kordi.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pl.kucharski.Kordi.config.PaginationConstants;
import pl.kucharski.Kordi.exception.CollectionNotFoundException;
import pl.kucharski.Kordi.exception.CommentNotFoundException;
import pl.kucharski.Kordi.model.comment.CommentDTO;
import pl.kucharski.Kordi.model.comment.CreateCommentDTO;
import pl.kucharski.Kordi.service.collection.CommentService;

import java.net.URI;
import java.util.List;

@RestController
public class CommentController {

    CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/collections/{collectionId}/comments")
    ResponseEntity<?> addComment(@PathVariable("collectionId") Long collectionId, @RequestBody CreateCommentDTO comment){
        try {
            CommentDTO addedComment = commentService.addComment(collectionId, comment);
            return ResponseEntity.created(
                    URI.create("/collections/" + collectionId + "/comments/" + addedComment.getId())
            ).body(addedComment);
        } catch (CollectionNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection with id " + collectionId + " not found");
        }
    }

    @DeleteMapping("/collections/{collectionId}/comments/{commentId}")
    ResponseEntity<?> removeComment(@PathVariable("collectionId") Long collectionId, @PathVariable("commentId") Long commentId){
        try {
            commentService.removeComment(collectionId, commentId);
            return ResponseEntity.ok("Comment with id " + commentId + " deleted");
        } catch (CollectionNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection with id " + collectionId + " not found");
        } catch (CommentNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment with id " + commentId +
                    " in collection with id " + collectionId + " not found");
        }
    }

    @GetMapping("/collections/{collectionId}/comments")
    ResponseEntity<?> getCommentFromCollection(@PathVariable("collectionId") Long collectionId,
                                               @RequestParam(value = "pageNo",
                                                       defaultValue = PaginationConstants.DEFAULT_PAGE_NUMBER,
                                                       required = false) int pageNo,
                                               @RequestParam(value = "pageSize",
                                                       defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE,
                                                       required = false) int pageSize){
        try {
            List<CommentDTO> comments = commentService.getAllComments(collectionId, PageRequest.of(pageNo, pageSize));
            return ResponseEntity.ok(comments);
        } catch (CollectionNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection with id " + collectionId + " not found");
        }
    }

}
