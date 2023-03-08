package pl.kucharski.Kordi;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import pl.kucharski.Kordi.model.address.Address;
import pl.kucharski.Kordi.model.address.AddressDTO;
import pl.kucharski.Kordi.model.address.AddressMapper;
import pl.kucharski.Kordi.model.collection.Collection;
import pl.kucharski.Kordi.model.collection.Collection.CollectionBuilder;
import pl.kucharski.Kordi.model.collection.CollectionDTO;
import pl.kucharski.Kordi.model.collection.CollectionDTO.CollectionDTOBuilder;
import pl.kucharski.Kordi.model.collection_item.CollectionItem;
import pl.kucharski.Kordi.model.collection_item.CollectionItemDTO;
import pl.kucharski.Kordi.model.collection_item.CollectionItemMapper;
import pl.kucharski.Kordi.model.user.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class CollectionMapperTestImpl  {

    private final AddressMapper addressMapper = Mappers.getMapper(AddressMapper.class);

    private final CollectionItemMapper collectionItemMapper = Mappers.getMapper(CollectionItemMapper.class);

    public CollectionMapperTestImpl() {
    }

    public CollectionDTO mapToCollectionDTO(Collection collection) {
        if (collection == null) {
            return null;
        } else {
            CollectionDTOBuilder collectionDTO = CollectionDTO.builder();
            collectionDTO.userFirstname(this.collectionUserFirstName(collection));
            collectionDTO.userLastname(this.collectionUserLastName(collection));
            collectionDTO.id(collection.getId());
            collectionDTO.title(collection.getTitle());
            collectionDTO.description(collection.getDescription());
            collectionDTO.startTime(collection.getStartTime());
            collectionDTO.endTime(collection.getEndTime());
            collectionDTO.userId(collection.getUserId());
            collectionDTO.addresses(this.addressListToAddressDTOList(collection.getAddresses()));
            collectionDTO.items(this.collectionItemListToCollectionItemDTOList(collection.getItems()));
            return collectionDTO.build();
        }
    }

    public Collection mapToCollection(CollectionDTO collection) {
        if (collection == null) {
            return null;
        } else {
            CollectionBuilder collection1 = Collection.builder();
            collection1.title(collection.getTitle());
            collection1.description(collection.getDescription());
            collection1.startTime(collection.getStartTime());
            collection1.endTime(collection.getEndTime());
            collection1.status(collection.getStatus());
            collection1.userId(collection.getUserId());
            collection1.addresses(this.addressDTOListToAddressList(collection.getAddresses()));
            collection1.items(this.collectionItemDTOListToCollectionItemList(collection.getItems()));
            return collection1.build();
        }
    }

    private String collectionUserFirstName(Collection collection) {
        if (collection == null) {
            return null;
        } else {
            User user = collection.getUser();
            if (user == null) {
                return null;
            } else {
                return user.getFirstName();
            }
        }
    }

    private String collectionUserLastName(Collection collection) {
        if (collection == null) {
            return null;
        } else {
            User user = collection.getUser();
            if (user == null) {
                return null;
            } else {
                return user.getLastName();
            }
        }
    }

    protected List<AddressDTO> addressListToAddressDTOList(List<Address> list) {
        if (list == null) {
            return null;
        } else {
            List<AddressDTO> list1 = new ArrayList<>(list.size());

            for (Address address : list) {
                list1.add(this.addressMapper.mapToAddressDTO(address));
            }

            return list1;
        }
    }

    protected List<CollectionItemDTO> collectionItemListToCollectionItemDTOList(List<CollectionItem> list) {
        if (list == null) {
            return null;
        } else {
            List<CollectionItemDTO> list1 = new ArrayList<>(list.size());

            for (CollectionItem collectionItem : list) {
                list1.add(this.collectionItemMapper.mapToCollectionItemDTO(collectionItem));
            }

            return list1;
        }
    }

    protected List<Address> addressDTOListToAddressList(List<AddressDTO> list) {
        if (list == null) {
            return null;
        } else {
            List<Address> list1 = new ArrayList<>(list.size());

            for (AddressDTO addressDTO : list) {
                list1.add(this.addressMapper.mapToAddress(addressDTO));
            }

            return list1;
        }
    }

    protected List<CollectionItem> collectionItemDTOListToCollectionItemList(List<CollectionItemDTO> list) {
        if (list == null) {
            return null;
        } else {
            List<CollectionItem> list1 = new ArrayList<>(list.size());

            for (CollectionItemDTO collectionItemDTO : list) {
                list1.add(this.collectionItemMapper.mapToCollectionItem(collectionItemDTO));
            }

            return list1;
        }
    }
}
