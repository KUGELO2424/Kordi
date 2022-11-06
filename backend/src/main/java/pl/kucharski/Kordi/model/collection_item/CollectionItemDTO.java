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

import static pl.kucharski.Kordi.config.ErrorCodes.COLLECTION_ITEM_CURRENT_BIGGER_THAN_MAX;
import static pl.kucharski.Kordi.config.ErrorCodes.ITEM_CATEGORY_CANNOT_BE_EMPTY;
import static pl.kucharski.Kordi.config.ErrorCodes.ITEM_NAME_CANNOT_BE_EMPTY;
import static pl.kucharski.Kordi.config.ErrorCodes.ITEM_TYPE_CANNOT_BE_EMPTY;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
@ItemValidator(message = COLLECTION_ITEM_CURRENT_BIGGER_THAN_MAX)
public class CollectionItemDTO {

    private Long id;
    @NotBlank(message = ITEM_NAME_CANNOT_BE_EMPTY)
    private String name;
    @NotNull(message = ITEM_TYPE_CANNOT_BE_EMPTY)
    private ItemType type;
    @NotNull(message = ITEM_CATEGORY_CANNOT_BE_EMPTY)
    private ItemCategory category;
    private int currentAmount;
    private int maxAmount;

}
