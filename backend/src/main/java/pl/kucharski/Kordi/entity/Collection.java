package pl.kucharski.Kordi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

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
public class Collection extends BaseEntity{

    private String title;
    private String description;

    @CreationTimestamp
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "collection_id")
    private List<Address> addresses = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "collection_id")
    private List<CollectionItem> items = new ArrayList<>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "collection_id")
    private List<SubmittedItem> submittedItems = new ArrayList<>();

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

    public Collection(String title, String description, LocalDateTime startTime, LocalDateTime endTime, User user,
                      List<Address> addresses, List<CollectionItem> items, List<SubmittedItem> submittedItems) {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.user = user;
        this.addresses = addresses;
        this.items = items;
        this.submittedItems = submittedItems;
    }

    public Collection() {
        super();
    }
}
