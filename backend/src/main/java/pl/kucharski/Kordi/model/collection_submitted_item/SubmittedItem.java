package pl.kucharski.Kordi.model.collection_submitted_item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import pl.kucharski.Kordi.model.BaseEntity;
import pl.kucharski.Kordi.model.collection.Collection;
import pl.kucharski.Kordi.model.collection_item.CollectionItem;
import pl.kucharski.Kordi.model.user.User;

import javax.persistence.*;
import java.util.Date;

/**
 * Entity that represents submitted collection item. It contains submit time and amount of donated item.
 * @see Collection
 *
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */

@Getter
@Entity
@Table(name = "submitted_item")
public class SubmittedItem extends BaseEntity {

    @Column(name = "amount")
    private int amount;

    @Column(name = "submit_time")
    @CreationTimestamp
    private Date submitTime;

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

    @ManyToOne
    @JoinColumn(name = "item_id", insertable = false, updatable = false)
    private CollectionItem collection_item;

    @Column(name = "item_id")
    private Long itemId;

    public SubmittedItem() {
    }

    public SubmittedItem(int amount, Date submitTime, Long userId, Long collectionId, Long itemId) {
        this.amount = amount;
        this.submitTime = submitTime;
        this.userId = userId;
        this.collectionId = collectionId;
        this.itemId = itemId;
    }
}
