package pl.kucharski.Kordi.service.collection;

import pl.kucharski.Kordi.exception.AddressNotFoundException;
import pl.kucharski.Kordi.exception.CollectionNotFoundException;
import pl.kucharski.Kordi.exception.AddressAlreadyExistsInCollectionException;
import pl.kucharski.Kordi.model.address.AddressDTO;

/**
 * @author Grzegorz Kucharski gelo2424@wp.pl
 */

public interface CollectionAddressService {

    /**
     * Add new address to collection
     *
     * @param collectionId of collection
     * @param address address to add
     * @throws CollectionNotFoundException if no collection with given id
     * @throws AddressAlreadyExistsInCollectionException if address already exists in collection
     */
    void addCollectionAddress(Long collectionId, AddressDTO address);

    /**
     * Remove address from collection
     *
     * @param collectionId id of collection
     * @param addressId id of address to remove
     * @throws CollectionNotFoundException if no collection with given id
     * @throws AddressNotFoundException if no address with given id
     */
    void removeCollectionAddress(Long collectionId, Long addressId);

}
