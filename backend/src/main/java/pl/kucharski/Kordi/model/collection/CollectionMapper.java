package pl.kucharski.Kordi.model.collection;

import pl.kucharski.Kordi.model.address.AddressMapper;
import pl.kucharski.Kordi.model.collection_item.CollectionItemMapper;

import java.util.ArrayList;
import java.util.stream.Collectors;

public final class CollectionMapper {

    public static CollectionDTO mapCollectionDTOFromCollection(Collection collection) {
        return CollectionDTO.builder()
                .id(collection.getId())
                .title(collection.getTitle())
                .description(collection.getDescription())
                .startTime(collection.getStartTime())
                .endTime(collection.getEndTime())
                .userFirstname(collection.getUser().getFirstName())
                .userLastname(collection.getUser().getLastName())
                .addresses(collection.getAddresses()
                        .stream()
                        .map(AddressMapper::mapAddressDTOFromAddress).collect(Collectors.toList()))
                .items(collection.getItems()
                        .stream()
                        .map(CollectionItemMapper::mapCollectionItemDTOFromCollectionItem).collect(Collectors.toList()))
                .build();
    }

    public static Collection mapCollectionFromCollectionDTO(CollectionDTO collection) {
        return new Collection(collection.getTitle(),
                collection.getDescription(),
                collection.getStartTime(),
                collection.getEndTime(),
                collection.getUserId(),
                collection.getAddresses()
                        .stream()
                        .map(AddressMapper::mapAddressFromAddressDTO).collect(Collectors.toList()),
                collection.getItems()
                        .stream()
                        .map(CollectionItemMapper::mapCollectionItemFromCollectionItemDTO).collect(Collectors.toList()),
                new ArrayList<>());
    }

}
