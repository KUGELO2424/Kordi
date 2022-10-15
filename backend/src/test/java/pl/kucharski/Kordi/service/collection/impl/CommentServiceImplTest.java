package pl.kucharski.Kordi.service.collection.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import pl.kucharski.Kordi.CollectionData;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static pl.kucharski.Kordi.CollectionData.COMMENT_CONTENT;

@ExtendWith(MockitoExtension.class)
@Transactional
class CommentServiceImplTest {

    private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);
    private CreateCommentDTO CREATE_COMMENT_DTO;
    private Collection COLLECTION_WITH_ID;
    private List<Comment> COMMENTS_WITH_ID;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CollectionRepository collectionRepository;

    @InjectMocks
    private CommentServiceImpl underTest;

    @BeforeEach
    void setUp() {
        underTest = new CommentServiceImpl(commentRepository, collectionRepository, commentMapper);
        CREATE_COMMENT_DTO = CollectionData.createCommentDTO();
        COLLECTION_WITH_ID = CollectionData.createCollectionWithId();
        COMMENTS_WITH_ID = CollectionData.createListOfComments();
    }

    @Test
    void shouldAddComment() {
        // given
        given(collectionRepository.findById(1L)).willReturn(Optional.of(COLLECTION_WITH_ID));

        // when
        underTest.addComment(1L, CREATE_COMMENT_DTO);
        Collection foundCollection = collectionRepository.findById(1L).orElse(null);

        // then
        assertNotNull(foundCollection);
        assertEquals(1, foundCollection.getComments().size());
        assertEquals(COMMENT_CONTENT, foundCollection.getComments().get(0).getContent());
    }

    @Test
    void shouldThrowCollectionNotFound() {
        // given
        given(collectionRepository.findById(1L)).willReturn(Optional.empty());

        // when + then
        assertThrows(CollectionNotFoundException.class, () -> underTest.addComment(1L, CREATE_COMMENT_DTO));
    }

    @Test
    void shouldRemoveComment() {
        // given
        given(collectionRepository.findById(1L)).willReturn(Optional.of(COLLECTION_WITH_ID));
        COLLECTION_WITH_ID.setComments(COMMENTS_WITH_ID);
        assertEquals(1, COLLECTION_WITH_ID.getComments().size());

        // when
        underTest.removeComment(1L, 1L);
        Collection foundCollection = collectionRepository.findById(1L).orElse(null);

        // then
        assertNotNull(foundCollection);
        assertEquals(0, foundCollection.getComments().size());
    }

    @Test
    void shouldThrowCollectionNotFoundOnRemove() {
        // given
        given(collectionRepository.findById(1L)).willReturn(Optional.empty());

        // when + then
        assertThrows(CollectionNotFoundException.class, () -> underTest.removeComment(1L, 1L));
    }

    @Test
    void shouldThrowCommentNotFoundOnRemove() {
        // given
        given(collectionRepository.findById(1L)).willReturn(Optional.of(COLLECTION_WITH_ID));

        // when + then
        assertThrows(CommentNotFoundException.class, () -> underTest.removeComment(1L, 1L));
    }

    @Test
    void shouldThrowCollectionNotFoundOnGetAllComments() {
        // given
        given(collectionRepository.existsById(1L)).willReturn(false);

        // when + then
        assertThrows(CollectionNotFoundException.class, () -> underTest.getAllComments(1L, CollectionData.PAGING));
    }

    @Test
    void shouldReturnListOfComments() {
        // given
        COLLECTION_WITH_ID.setComments(COMMENTS_WITH_ID);
        given(commentRepository.getAllByCollectionId(1L, CollectionData.PAGING)).willReturn(COMMENTS_WITH_ID);
        given(collectionRepository.existsById(1L)).willReturn(true);

        // when
        List<CommentDTO> comments = underTest.getAllComments(1L, CollectionData.PAGING);

        // then
        assertNotNull(comments);
        assertEquals(1, comments.size());
    }

}