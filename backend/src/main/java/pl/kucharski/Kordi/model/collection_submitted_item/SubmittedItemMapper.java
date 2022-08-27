package pl.kucharski.Kordi.model.collection_submitted_item;

public final class SubmittedItemMapper {

    public static SubmittedItemDTO mapSubmittedItemDTOFromSubmittedItem(SubmittedItem item) {
        return SubmittedItemDTO.builder()
                .amount(item.getAmount())
                .submitTime(item.getSubmitTime())
                .username(item.getUser().getUsername())
                .userId(item.getUser().getId())
                .collectionId(item.getCollection().getId())
                .collectionItemId(item.getCollection_item().getId())
                .build();
    }

    public static SubmittedItem mapSubmittedItemFromSubmittedItemDTO(SubmittedItemDTO item) {
        return new SubmittedItem(item.getAmount(), item.getSubmitTime(), item.getUserId(),
                item.getCollectionId(), item.getCollectionItemId());
    }

}
