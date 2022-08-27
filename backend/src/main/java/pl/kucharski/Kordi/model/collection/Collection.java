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
import java.util.Objects;

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
    @JoinColumn(name = "collection_id", nullable = false)
    private List<Address> addresses;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "collection_id", nullable = false)
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
        setAddresses(addresses);
        setItems(items);
        setSubmittedItems(submittedItems);
    }

    public Collection(String title, String description, LocalDateTime startTime, LocalDateTime endTime,
                      Long userId, List<Address> addresses, List<CollectionItem> items,
                      List<SubmittedItem> submittedItems) {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.userId = userId;
        setAddresses(addresses);
        setItems(items);
        setSubmittedItems(submittedItems);
    }

    public Collection() {
        super();
    }

    public void addItem(CollectionItem item) {
        this.items.add(item);
    }

    public void addAddress(Address address) {
        this.addresses.add(address);
    }

    private void setAddresses(List<Address> addresses) {
        this.addresses = Objects.requireNonNullElseGet(addresses, ArrayList::new);
    }

    private void setItems(List<CollectionItem> items) {
        this.items = Objects.requireNonNullElseGet(items, ArrayList::new);
    }

    private void setSubmittedItems(List<SubmittedItem> submittedItems) {
        this.submittedItems = Objects.requireNonNullElseGet(submittedItems, ArrayList::new);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
