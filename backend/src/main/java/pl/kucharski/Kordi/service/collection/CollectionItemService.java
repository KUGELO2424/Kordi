package pl.kucharski.Kordi.service.collection;

import pl.kucharski.Kordi.exception.CollectionItemNotFoundException;
import pl.kucharski.Kordi.exception.CollectionNotFoundException;
import pl.kucharski.Kordi.model.collection.CollectionDTO;
import pl.kucharski.Kordi.model.collection_item.CollectionItemDTO;
import pl.kucharski.Kordi.model.collection_submitted_item.SubmittedItemDTO;

import java.util.List;

/**
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */

public interface CollectionItemService {

    /**
     * Add new item to collection
     *
     * @param collectionId of collection
     * @param collectionItem item to add
     * @throws CollectionNotFoundException if no collection with given id
     */
    void addCollectionItem(Long collectionId, CollectionItemDTO collectionItem);

    /**
     * Update current and max amount of item in collection
     *
     * @param collectionId id of collection
     * @param itemId id of item
     * @param currentAmount new current amount of item
     * @param maxAmount new max amount of item
     * @throws CollectionNotFoundException if no collection with given id
     * @throws CollectionItemNotFoundException if no collection item with given id in collection
     */
    CollectionDTO updateCollectionItem(long collectionId, long itemId, int currentAmount, int maxAmount);

    /**
     * Submit item
     *
     * @param item item to submit
     * @throws CollectionNotFoundException if no collection with given id
     * @throws CollectionItemNotFoundException if no collection item with given id in collection
     */
    CollectionDTO submitItem(SubmittedItemDTO item);

    /**
     * Get all submitted items from specific collection
     *
     * @param collectionId id of collection
     * @throws CollectionNotFoundException if collection not found
     */
    List<SubmittedItemDTO> getSubmittedItems(long collectionId);

    /**
     * Get all submitted items from specific item from collection
     *
     * @param collectionId id of collection
     * @param itemId id of item
     * @throws CollectionNotFoundException if no collection with given id
     * @throws CollectionItemNotFoundException if no collection item with given id in collection
     */
    List<SubmittedItemDTO> getSubmittedItemsForSpecificItem(long collectionId, long itemId);
}
