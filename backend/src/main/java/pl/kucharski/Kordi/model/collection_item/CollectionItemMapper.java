package pl.kucharski.Kordi.model.collection_item;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CollectionItemMapper {

    CollectionItemDTO mapToCollectionItemDTO(CollectionItem collectionItem);
    CollectionItem mapToCollectionItem(CollectionItemDTO collectionItem);

}
