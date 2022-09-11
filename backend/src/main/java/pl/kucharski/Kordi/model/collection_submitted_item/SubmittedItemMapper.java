package pl.kucharski.Kordi.model.collection_submitted_item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class SubmittedItemMapper {

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "collectionItemId", source = "itemId")
    public abstract SubmittedItemDTO mapToSubmittedItemDTO(SubmittedItem item);

    @Mapping(target = "itemId", source = "collectionItemId")
    public abstract SubmittedItem mapToSubmittedItem(SubmittedItemDTO item);

}
