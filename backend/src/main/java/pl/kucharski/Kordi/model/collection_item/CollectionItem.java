package pl.kucharski.Kordi.model.collection_item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kucharski.Kordi.enums.ItemCategory;
import pl.kucharski.Kordi.enums.ItemType;
import pl.kucharski.Kordi.model.BaseEntity;
import pl.kucharski.Kordi.model.collection.Collection;

import javax.persistence.*;

/**
 * Entity that represents collection item. User can choose type, which determine the amount of collected items.
 * It can be some number or weight in kilograms. Also, user can choose UNLIMITED then currentAmount and maxAmount
 * will be 0.
 * @see Collection
 *
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "collection_item")
public class CollectionItem extends BaseEntity {

    private String name;

    @Column(columnDefinition="ENUM('AMOUNT', 'WEIGHT', 'UNLIMITED')")
    @Enumerated(EnumType.STRING)
    private ItemType type;

    @Column(columnDefinition="ENUM('CLOTHES', 'FOOD', 'ANIMALS', 'CHILDREN', 'MEDICINE', 'ELECTRONIC', 'OTHER')")
    @Enumerated(EnumType.STRING)
    private ItemCategory category;

    @Column(name = "current_amount")
    private int currentAmount;

    @Column(name = "max_amount")
    private int maxAmount;



}
