package pl.kucharski.Kordi.service.collection;

import pl.kucharski.Kordi.exception.CollectionNotFoundException;
import pl.kucharski.Kordi.exception.AddressAlreadyExistsInCollectionException;
import pl.kucharski.Kordi.exception.AddressNotFoundException;
import pl.kucharski.Kordi.model.address.AddressDTO;

/**
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
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
     * @param collectionId of collection
     * @param address address to remove
     * @throws CollectionNotFoundException if no collection with given id
     * @throws AddressNotFoundException if address not found in collection
     */
    void removeCollectionAddress(Long collectionId, AddressDTO address);

}
