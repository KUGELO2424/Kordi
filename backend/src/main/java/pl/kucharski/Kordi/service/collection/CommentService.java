package pl.kucharski.Kordi.service.collection;

import org.springframework.data.domain.Pageable;
import pl.kucharski.Kordi.exception.CollectionNotFoundException;
import pl.kucharski.Kordi.exception.CommentNotFoundException;
import pl.kucharski.Kordi.model.comment.CommentDTO;
import pl.kucharski.Kordi.model.comment.CreateCommentDTO;

import java.util.List;

/**
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */

public interface CommentService {

    /**
     * Add new comment to collection
     *
     * @param collectionId id of collection
     * @param comment comment to add
     * @throws CollectionNotFoundException if no collection with given id
     */
    CommentDTO addComment(Long collectionId, CreateCommentDTO comment);

    /**
     * Remove comment from collection
     *
     * @param collectionId id of collection
     * @param commentId id of comment to delete
     * @throws CollectionNotFoundException if no collection with given id
     * @throws CommentNotFoundException if no comment with given id in collection
     */
    void removeComment(Long collectionId, Long commentId);

    /**
     * Get all comments from collection
     *
     * @param collectionId id of collection
     * @throws CollectionNotFoundException if no collection with given id
     */
    List<CommentDTO> getAllComments(Long collectionId, Pageable pageable);
}
