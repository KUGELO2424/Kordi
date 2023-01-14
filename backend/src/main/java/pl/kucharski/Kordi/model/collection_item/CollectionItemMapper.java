package pl.kucharski.Kordi.model.collection_item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pl.kucharski.Kordi.enums.ItemType;

@Mapper(componentModel = "spring")
public interface CollectionItemMapper {

    CollectionItemDTO mapToCollectionItemDTO(CollectionItem collectionItem);

    @Mapping(target = "maxAmount", source = ".", qualifiedByName = "mapMaxAmount")
    CollectionItem mapToCollectionItem(CollectionItemDTO collectionItem);

    @Named("mapMaxAmount")
    static int mapMaxAmount(CollectionItemDTO collectionItem) {
        if (collectionItem.getType().equals(ItemType.UNLIMITED)) {
            return Integer.MAX_VALUE;
        } else {
            return collectionItem.getMaxAmount();
        }
    }
}
