package pl.kucharski.Kordi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Entity that represents collection created by User. Collection can be located in multiple addresses.
 * It contains list of items and submitted items by Users.
 * @see User
 *
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "collection")
public class Collection extends BaseEntity{

    private String title;
    private String description;

    @CreationTimestamp
    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

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

}
