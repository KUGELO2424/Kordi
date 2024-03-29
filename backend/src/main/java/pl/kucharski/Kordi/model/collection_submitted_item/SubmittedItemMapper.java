package pl.kucharski.Kordi.model.collection_submitted_item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubmittedItemMapper {

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "collectionItemId", source = "itemId")
    @Mapping(target = "itemName", source = "collection_item.name")
    @Mapping(target = "itemType", source = "collection_item.type")
    SubmittedItemDTO mapToSubmittedItemDTO(SubmittedItem item);

    @Mapping(target = "itemId", source = "collectionItemId")
    SubmittedItem mapToSubmittedItem(SubmittedItemDTO item);

}
