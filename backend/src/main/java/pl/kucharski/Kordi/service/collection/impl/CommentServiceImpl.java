package pl.kucharski.Kordi.service.collection.impl;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kucharski.Kordi.exception.CollectionNotFoundException;
import pl.kucharski.Kordi.exception.CommentNotFoundException;
import pl.kucharski.Kordi.model.collection.Collection;
import pl.kucharski.Kordi.model.comment.Comment;
import pl.kucharski.Kordi.model.comment.CommentDTO;
import pl.kucharski.Kordi.model.comment.CommentMapper;
import pl.kucharski.Kordi.model.comment.CreateCommentDTO;
import pl.kucharski.Kordi.repository.CollectionRepository;
import pl.kucharski.Kordi.repository.CommentRepository;
import pl.kucharski.Kordi.service.collection.CommentService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CollectionRepository collectionRepository;
    private final CommentMapper commentMapper;

    public CommentServiceImpl(CommentRepository commentRepository, CollectionRepository collectionRepository,
                              CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
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
                .orElseThrow(CollectionNotFoundException::new);
        foundCollection.addComment(comment);
        return commentMapper.mapToCommentDTO(commentRepository.save(comment));
    }

    /**
     * @see CommentService#removeComment(Long, Long)
     */
    @Override
    @Transactional
    public void removeComment(Long collectionId, Long commentId) {
        Collection foundCollection = collectionRepository.findById(collectionId)
                .orElseThrow(CollectionNotFoundException::new);
        Comment commentToDelete = foundCollection.getComments().stream()
                .filter(comment -> comment.getId().equals(commentId))
                .findFirst()
                .orElseThrow(CommentNotFoundException::new);
        foundCollection.getComments().remove(commentToDelete);
    }

    /**
     * @see CommentService#getAllComments(Long, Pageable)
     */
    @Override
    public List<CommentDTO> getAllComments(Long collectionId, Pageable pageable) {
        if (!collectionRepository.existsById(collectionId)) {
            throw new CollectionNotFoundException("Collection with id " + collectionId + " not found");
        }
        return commentRepository.getAllByCollectionId(collectionId, pageable).stream()
                .map(commentMapper::mapToCommentDTO)
                .collect(Collectors.toList());
    }
}
