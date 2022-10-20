package pl.kucharski.Kordi.service.collection.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kucharski.Kordi.aop.IsCollectionOwner;
import pl.kucharski.Kordi.exception.AddressAlreadyExistsInCollectionException;
import pl.kucharski.Kordi.exception.CollectionNotFoundException;
import pl.kucharski.Kordi.model.address.Address;
import pl.kucharski.Kordi.model.address.AddressDTO;
import pl.kucharski.Kordi.model.address.AddressMapper;
import pl.kucharski.Kordi.model.collection.Collection;
import pl.kucharski.Kordi.repository.CollectionRepository;
import pl.kucharski.Kordi.service.collection.CollectionAddressService;

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
                .orElseThrow(CollectionNotFoundException::new);
        collection.getAddresses().stream()
                .filter(address -> address.equals(addressToAdd))
                .findFirst()
                .ifPresent(address -> {
                    throw new AddressAlreadyExistsInCollectionException();
                });
        collection.addAddress(addressToAdd);
    }

    /**
     * @see CollectionAddressService#removeCollectionAddress(Long, AddressDTO)
     */
    @Override
    @Transactional
    @IsCollectionOwner
    public void removeCollectionAddress(Long collectionId, AddressDTO addressDTO) {
        Address addressToRemove = addressMapper.mapToAddress(addressDTO);
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(CollectionNotFoundException::new);
        collection.getAddresses().stream()
                .filter(address -> address.getCity().equals(addressToRemove.getCity())
                        && address.getStreet().equals(addressToRemove.getStreet()))
                .findFirst()
                .ifPresent(address -> collection.getAddresses().remove(address));
    }

}
