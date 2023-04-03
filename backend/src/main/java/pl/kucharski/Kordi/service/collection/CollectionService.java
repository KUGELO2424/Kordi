package pl.kucharski.Kordi.service.collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.kucharski.Kordi.enums.CollectionStatus;
import pl.kucharski.Kordi.enums.ItemCategory;
import pl.kucharski.Kordi.exception.CollectionNotFoundException;
import pl.kucharski.Kordi.model.collection.CollectionDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Grzegorz Kucharski gelo2424@wp.pl
 */

public interface CollectionService {

    /**
     * Find collection by given
     *
     * @param id of collection
     * @return CollectionDTO object if collection found
     * @throws CollectionNotFoundException if collection not found
     */
    CollectionDTO getCollectionById(long id);

    /**
     * Find collections by creator username
     *
     * @param username of creator
     * @param pageable pagination information
     * @return List of found collections. If no collections found, return empty list
     */
    List<CollectionDTO> getCollectionsByUser(String username, Pageable pageable);

    /**
     * Find collections with filtering. Each param can be empty, then all records match to this param
     *
     * @param title title of collection that you are looking for
     * @param city city of collection that you are looking for
     * @param street street of collection that you are looking for
     * @param itemName name of item that you are looking for
     * @param status status of collection
     * @param categories list of search categories
     * @param pageable pagination information
     * @return Page of found collections. If no collections found, return empty list
     */
    Page<CollectionDTO> getCollectionsWithFiltering(String title, String city, String street, String itemName, CollectionStatus status,
                                                    List<ItemCategory> categories, Pageable pageable);

    /**
     * Save new collection
     *
     * @param collection object of created collection
     * @return saved collection
     */
    CollectionDTO saveCollection(CollectionDTO collection);

    /**
     * Update basic information about collection. Put null or empty string as a parameter to leave old value.
     *
     * @param id of collection
     * @param title new title for collection
     * @param description new description for collection
     * @param endTime new endTime for collection
     * @throws CollectionNotFoundException if no collection with given id
     */
    CollectionDTO updateCollection(long id, String title, String description, LocalDateTime endTime);

}
