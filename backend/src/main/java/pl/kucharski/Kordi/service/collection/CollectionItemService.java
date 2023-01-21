package pl.kucharski.Kordi.service.collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.kucharski.Kordi.exception.CollectionItemNotFoundException;
import pl.kucharski.Kordi.exception.CollectionNotFoundException;
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
    CollectionItemDTO updateCollectionItem(long collectionId, long itemId, int currentAmount, int maxAmount);

    /**
     * Submit items
     *
     * @param items items to submit
     * @throws CollectionNotFoundException if no collection with given id
     * @throws CollectionItemNotFoundException if no collection item with given id in collection
     */
    List<CollectionItemDTO> submitItems(long collectionId, List<SubmittedItemDTO> items);

    /**
     * Submit item
     *
     * @param item item to submit
     * @throws CollectionNotFoundException if no collection with given id
     * @throws CollectionItemNotFoundException if no collection item with given id in collection
     */
    CollectionItemDTO submitItem(long collectionId, long itemId, SubmittedItemDTO item);

    /**
     * Get all submitted items from specific collection
     *
     * @param collectionId id of collection
     * @throws CollectionNotFoundException if collection not found
     */
    List<SubmittedItemDTO> getSubmittedItems(long collectionId);

    /**
     * Get submitted items for user
     *
     * @param username user username
     * @throws CollectionNotFoundException if collection not found
     */
    Page<SubmittedItemDTO> getSubmittedItemsForUser(String username, Pageable pageable);

    /**
     * Get last submitted items from specific collection
     *
     * @param collectionId id of collection
     * @param numberOfSubmittedItems number of submitted items to get
     * @throws CollectionNotFoundException if collection not found
     */
    List<SubmittedItemDTO> getLastSubmittedItems(long collectionId, int numberOfSubmittedItems);

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
