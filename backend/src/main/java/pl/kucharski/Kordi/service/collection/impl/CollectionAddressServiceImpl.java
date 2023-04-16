package pl.kucharski.Kordi.service.collection.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kucharski.Kordi.aop.IsCollectionOwner;
import pl.kucharski.Kordi.exception.AddressAlreadyExistsInCollectionException;
import pl.kucharski.Kordi.exception.AddressNotFoundException;
import pl.kucharski.Kordi.exception.CollectionNotFoundException;
import pl.kucharski.Kordi.model.address.Address;
import pl.kucharski.Kordi.model.address.AddressDTO;
import pl.kucharski.Kordi.model.address.AddressMapper;
import pl.kucharski.Kordi.model.collection.Collection;
import pl.kucharski.Kordi.repository.CollectionRepository;
import pl.kucharski.Kordi.service.collection.CollectionAddressService;

import static pl.kucharski.Kordi.config.ErrorCodes.ADDRESS_EXISTS;
import static pl.kucharski.Kordi.config.ErrorCodes.ADDRESS_NOT_FOUND;
import static pl.kucharski.Kordi.config.ErrorCodes.COLLECTION_NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class CollectionAddressServiceImpl implements CollectionAddressService {

    private final AddressMapper addressMapper;
    private final CollectionRepository collectionRepository;

    public CollectionAddressServiceImpl(AddressMapper addressMapper, CollectionRepository collectionRepository) {
        this.addressMapper = addressMapper;
        this.collectionRepository = collectionRepository;
    }

    /**
     * @see CollectionAddressService#addCollectionAddress(Long, AddressDTO)
     */
    @Override
    @Transactional
    @IsCollectionOwner
    public void addCollectionAddress(Long collectionId, AddressDTO addressDTO) {
        Address addressToAdd = addressMapper.mapToAddress(addressDTO);
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CollectionNotFoundException(COLLECTION_NOT_FOUND));
        collection.getAddresses().stream()
                .filter(address -> address.equals(addressToAdd))
                .findFirst()
                .ifPresent(address -> {
                    throw new AddressAlreadyExistsInCollectionException(ADDRESS_EXISTS);
                });
        collection.addAddress(addressToAdd);
    }

    /**
     * @see CollectionAddressService#removeCollectionAddress(Long, Long)
     */
    @Override
    @Transactional
    @IsCollectionOwner
    public void removeCollectionAddress(Long collectionId, Long addressId) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CollectionNotFoundException(COLLECTION_NOT_FOUND));
        Address foundAddress = collection.getAddresses().stream()
                .filter(address -> address.getId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new AddressNotFoundException(ADDRESS_NOT_FOUND));
        collection.getAddresses().remove(foundAddress);
    }

}
