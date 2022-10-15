package pl.kucharski.Kordi.model.collection_item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.kucharski.Kordi.enums.ItemType;
import pl.kucharski.Kordi.validator.ItemValidator;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
@ItemValidator(message = "Current amount cannot be smaller than max amount")
public class CollectionItemDTO {

    private Long id;
    @NotBlank(message = "Name of item cannot be empty")
    private String name;
    private ItemType type;
    private int currentAmount;
    private int maxAmount;

}
