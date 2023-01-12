package pl.kucharski.Kordi.service.collection.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kucharski.Kordi.aop.IsCollectionOwner;
import pl.kucharski.Kordi.exception.CollectionNotFoundException;
import pl.kucharski.Kordi.exception.CommentNotFoundException;
import pl.kucharski.Kordi.exception.UserNotFoundException;
import pl.kucharski.Kordi.model.collection.Collection;
import pl.kucharski.Kordi.model.comment.Comment;
import pl.kucharski.Kordi.model.comment.CommentDTO;
import pl.kucharski.Kordi.model.comment.CommentMapper;
import pl.kucharski.Kordi.model.comment.CreateCommentDTO;
import pl.kucharski.Kordi.repository.CollectionRepository;
import pl.kucharski.Kordi.repository.CommentRepository;
import pl.kucharski.Kordi.repository.UserRepository;
import pl.kucharski.Kordi.service.collection.CommentService;

import java.util.List;
import java.util.stream.Collectors;

import static pl.kucharski.Kordi.config.ErrorCodes.COLLECTION_NOT_FOUND;
import static pl.kucharski.Kordi.config.ErrorCodes.COMMENT_NOT_FOUND;
import static pl.kucharski.Kordi.config.ErrorCodes.USER_NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CollectionRepository collectionRepository;
    private final CommentMapper commentMapper;

    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository, CollectionRepository collectionRepository,
                              CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.collectionRepository = collectionRepository;
        this.commentMapper = commentMapper;
    }

    /**
     * @see CommentService#addComment(Long, CreateCommentDTO)
     */
    @Override
    @Transactional
    public CommentDTO addComment(Long collectionId, CreateCommentDTO commentDTO) {
        Comment comment = commentMapper.mapToComment(commentDTO);
        Collection foundCollection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CollectionNotFoundException(COLLECTION_NOT_FOUND));
        userRepository.findById(commentDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        foundCollection.addComment(comment);
        return commentMapper.mapToCommentDTO(commentRepository.save(comment));
    }

    /**
     * @see CommentService#removeComment(Long, Long)
     */
    @Override
    @Transactional
    @IsCollectionOwner
    public void removeComment(Long collectionId, Long commentId) {
        Collection foundCollection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CollectionNotFoundException(COLLECTION_NOT_FOUND));
        Comment commentToDelete = foundCollection.getComments().stream()
                .filter(comment -> comment.getId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new CommentNotFoundException(COMMENT_NOT_FOUND));
        foundCollection.getComments().remove(commentToDelete);
    }

    /**
     * @see CommentService#getAllComments(Long, Pageable)
     */
    @Override
    public Page<CommentDTO> getAllComments(Long collectionId, Pageable pageable) {
        if (!collectionRepository.existsById(collectionId)) {
            throw new CollectionNotFoundException(COLLECTION_NOT_FOUND);
        }
        Page<Comment> commentsPage = commentRepository.getAllByCollectionId(collectionId, pageable);
        if (commentsPage == null) {
            return Page.empty();
        }
        List<CommentDTO> comments = commentsPage.stream()
                .map(commentMapper::mapToCommentDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(comments, pageable, commentsPage.getTotalElements());
    }
}
