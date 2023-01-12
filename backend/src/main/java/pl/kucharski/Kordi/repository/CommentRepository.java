package pl.kucharski.Kordi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.kucharski.Kordi.model.comment.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> getAllByCollectionId(Long collectionId, Pageable pageable);

}
