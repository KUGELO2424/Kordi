package pl.kucharski.Kordi.model.collection_item;

import lombok.*;
import pl.kucharski.Kordi.enums.ItemType;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class CollectionItemDTO {

    private Long id;
    private String name;
    private ItemType type;
    private int currentAmount;
    private int maxAmount;

}
