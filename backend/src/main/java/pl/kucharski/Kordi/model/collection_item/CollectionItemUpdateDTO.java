package pl.kucharski.Kordi.model.collection_item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class CollectionItemUpdateDTO {

    private int currentAmount;
    private int maxAmount;

}