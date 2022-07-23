package pl.kucharski.Kordi.model.collection_item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pl.kucharski.Kordi.enums.ItemType;

@Getter
@AllArgsConstructor
@ToString
@Builder
public class CollectionItemDTO {

    private Long id;
    private String name;
    private ItemType type;
    private int currentAmount;
    private int maxAmount;

}
