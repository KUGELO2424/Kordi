package pl.kucharski.Kordi.model.comment;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import pl.kucharski.Kordi.model.BaseEntity;
import pl.kucharski.Kordi.model.collection.Collection;
import pl.kucharski.Kordi.model.user.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "collection_comment")
public class Comment extends BaseEntity {

    @Column(name = "content")
    String content;

    @CreationTimestamp
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "collection_id", insertable = false, updatable = false)
    private Collection collection;

    @Column(name = "collection_id")
    private Long collectionId;

    public Comment() {

    }

    public Comment(Long id, String content, Long userId, Long collectionId) {
        super(id);
        this.content = content;
        this.userId = userId;
        this.collectionId = collectionId;
    }
}
