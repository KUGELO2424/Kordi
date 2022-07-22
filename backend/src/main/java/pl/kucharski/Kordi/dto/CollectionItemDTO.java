package pl.kucharski.Kordi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import pl.kucharski.Kordi.enums.ItemType;

@Getter
@AllArgsConstructor
@ToString
public class CollectionItemDTO {

    private Long id;
    private String name;
    private ItemType type;
    private int currentAmount;
    private int maxAmount;

}
