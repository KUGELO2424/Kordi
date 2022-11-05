package pl.kucharski.Kordi.model.collection_item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.kucharski.Kordi.enums.ItemCategory;
import pl.kucharski.Kordi.enums.ItemType;
import pl.kucharski.Kordi.validator.ItemValidator;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
    @NotNull(message = "Item type cannot be null")
    private ItemType type;
    @NotNull(message = "Item category cannot be null")
    private ItemCategory category;
    private int currentAmount;
    private int maxAmount;

}
