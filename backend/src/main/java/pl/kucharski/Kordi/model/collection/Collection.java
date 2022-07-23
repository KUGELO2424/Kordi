package pl.kucharski.Kordi.model.collection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import pl.kucharski.Kordi.model.BaseEntity;
import pl.kucharski.Kordi.model.address.Address;
import pl.kucharski.Kordi.model.collection_item.CollectionItem;
import pl.kucharski.Kordi.model.collection_submitted_item.SubmittedItem;
import pl.kucharski.Kordi.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity that represents collection created by User. Collection can be located in multiple addresses.
 * It contains list of items and submitted items by Users.
 * @see User
 *
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */

@Getter
@Entity
@Table(name = "collection")
@Builder
@AllArgsConstructor
public class Collection extends BaseEntity {

    private String title;
    private String description;

    @CreationTimestamp
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "user_id")
    private Long userId;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "collection_id")
    private List<Address> addresses;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "collection_id")
    private List<CollectionItem> items;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "collection_id")
    private List<SubmittedItem> submittedItems;

    public Collection(Long id, String title, String description, LocalDateTime startTime, LocalDateTime endTime,
                      User user, List<Address> addresses, List<CollectionItem> items,
                      List<SubmittedItem> submittedItems) {
        super(id);
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.user = user;
        this.addresses = addresses;
        this.items = items;
        this.submittedItems = submittedItems;
    }

    public Collection(String title, String description, LocalDateTime startTime, LocalDateTime endTime,
                      Long userId, List<Address> addresses, List<CollectionItem> items,
                      List<SubmittedItem> submittedItems) {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.userId = userId;
        this.addresses = addresses;
        this.items = items;
        this.submittedItems = submittedItems;
    }

    public Collection() {
        super();
    }
}
