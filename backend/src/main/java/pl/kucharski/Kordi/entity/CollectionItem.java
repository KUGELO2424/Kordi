package pl.kucharski.Kordi.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.kucharski.Kordi.enums.ItemType;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "collection_item")
public class CollectionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "type", columnDefinition="ENUM('AMOUNT', 'WEIGHT', 'UNLIMITED')")
    @Enumerated(EnumType.STRING)
    private ItemType type;

    @Column(name = "current_amount")
    private int currentAmount;

    @Column(name = "max_amount")
    private int maxAmount;



}
