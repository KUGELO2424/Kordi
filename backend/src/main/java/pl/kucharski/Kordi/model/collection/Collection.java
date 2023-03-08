package pl.kucharski.Kordi.model.collection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import pl.kucharski.Kordi.enums.CollectionStatus;
import pl.kucharski.Kordi.model.BaseEntity;
import pl.kucharski.Kordi.model.address.Address;
import pl.kucharski.Kordi.model.collection_item.CollectionItem;
import pl.kucharski.Kordi.model.collection_submitted_item.SubmittedItem;
import pl.kucharski.Kordi.model.comment.Comment;
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
 * @author Grzegorz Kucharski gelo2424@wp.pl
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

    @Column(name = "completed_time")
    private LocalDateTime completedTime;

    @Lob
    @Column(name = "image")
    private byte[] image;

    @Column(name = "donates")
    private Long donates;

    @Column(columnDefinition="ENUM('IN_PROGRESS', 'COMPLETED', 'ARCHIVED')")
    @Enumerated(EnumType.STRING)
    private CollectionStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "user_id")
    private Long userId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "collection_id", nullable = false)
    private List<Address> addresses;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "collection_id", nullable = false)
    private List<CollectionItem> items;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id")
    private List<SubmittedItem> submittedItems;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "collection_id", insertable = false, updatable = false)
    private List<Comment> comments;

    public Collection(Long id, String title, String description, LocalDateTime startTime, LocalDateTime endTime, Long donates,
                      CollectionStatus status, User user, List<Address> addresses, List<CollectionItem> items,
                      List<SubmittedItem> submittedItems, List<Comment> comments) {
        super(id);
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.user = user;
        setAddresses(addresses);
        setItems(items);
        setSubmittedItems(submittedItems);
        setComments(comments);
        setDonates(donates);
        setStatus(status);
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

    public void addSubmittedItem(SubmittedItem item) {
        this.submittedItems.add(item);
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
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

    public void setComments(List<Comment> comments) {
        this.comments = Objects.requireNonNullElseGet(comments, ArrayList::new);
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

    public void setCompletedTime(LocalDateTime completedTime) {
        this.completedTime = completedTime;
    }

    public void setDonates(Long donates) {
        this.donates = donates;
    }

    public void setStatus(CollectionStatus status) {
        this.status = status;
    }
}
