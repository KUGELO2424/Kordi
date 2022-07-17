package pl.kucharski.Kordi.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.kucharski.Kordi.enums.ItemType;

import javax.persistence.*;

/**
 * Entity that represents collection item. User can choose type, which determine the amount of collected items.
 * It can be some number or weight in kilograms. Also, user can choose UNLIMITED then currentAmount and maxAmount
 * will be 0.
 * @see Collection
 *
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "collection_item")
public class CollectionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Column(columnDefinition="ENUM('AMOUNT', 'WEIGHT', 'UNLIMITED')")
    @Enumerated(EnumType.STRING)
    private ItemType type;

    @Column(name = "current_amount")
    private int currentAmount;

    @Column(name = "max_amount")
    private int maxAmount;



}
