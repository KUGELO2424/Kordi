package pl.kucharski.Kordi.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * Entity that represents submitted collection item. It contains submit time and amount of donated item.
 * @see Collection
 *
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "submitted_item")
public class SubmittedItem extends BaseEntity{

    @Column(name = "amount")
    private int amount;

    @Column(name = "submit_time")
    @CreationTimestamp
    private Date submitTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "collection_id")
    private Collection collection;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private CollectionItem collection_item;
}
