package pl.kucharski.Kordi.model.collection_item;

public final class CollectionItemMapper {

    public static CollectionItemDTO mapCollectionItemDTOFromCollectionItem(CollectionItem item) {
        return CollectionItemDTO.builder()
                .id(item.getId())
                .name(item.getName())
                .type(item.getType())
                .currentAmount(item.getCurrentAmount())
                .maxAmount(item.getMaxAmount())
                .build();
    }

    public static CollectionItem mapCollectionItemFromCollectionItemDTO(CollectionItemDTO item) {
        return new CollectionItem(item.getName(), item.getType(), item.getCurrentAmount(), item.getMaxAmount());
    }


}
