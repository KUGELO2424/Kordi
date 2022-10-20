package pl.kucharski.Kordi.service.collection.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kucharski.Kordi.exception.CollectionItemException;
import pl.kucharski.Kordi.exception.CollectionItemNotFoundException;
import pl.kucharski.Kordi.exception.CollectionNotFoundException;
import pl.kucharski.Kordi.model.collection.Collection;
import pl.kucharski.Kordi.model.collection_item.CollectionItem;
import pl.kucharski.Kordi.model.collection_item.CollectionItemDTO;
import pl.kucharski.Kordi.model.collection_item.CollectionItemMapper;
import pl.kucharski.Kordi.model.collection_submitted_item.SubmittedItemDTO;
import pl.kucharski.Kordi.model.collection_submitted_item.SubmittedItemMapper;
import pl.kucharski.Kordi.repository.CollectionRepository;
import pl.kucharski.Kordi.service.collection.CollectionItemService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CollectionItemServiceImpl implements CollectionItemService {

    private final CollectionRepository collectionRepository;
    private final CollectionItemMapper itemMapper;
    private final SubmittedItemMapper submittedItemMapper;

    public CollectionItemServiceImpl(CollectionRepository collectionRepository, CollectionItemMapper itemMapper,
                                     SubmittedItemMapper submittedItemMapper) {
        this.collectionRepository = collectionRepository;
        this.itemMapper = itemMapper;
        this.submittedItemMapper = submittedItemMapper;
    }

    /**
     * @see CollectionItemService#addCollectionItem(Long, CollectionItemDTO)
     */
    @Override
    @Transactional
    public void addCollectionItem(Long collectionId, CollectionItemDTO collectionItemDTO) {
        CollectionItem collectionItem = itemMapper.mapToCollectionItem(collectionItemDTO);
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(CollectionNotFoundException::new);
        collection.addItem(collectionItem);
    }

    /**
     * @see CollectionItemService#updateCollectionItem(long, long, int, int)
     */
    @Override
    @Transactional
    public CollectionItemDTO updateCollectionItem(long collectionId, long itemId, int currentAmount, int maxAmount) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CollectionNotFoundException("Collection with id " + collectionId
                        + " not found in database"));
        CollectionItem foundItem = collection.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new CollectionNotFoundException("Item with id " + itemId
                        + " not found in collection with id " + collectionId));

        if (maxAmount < currentAmount) {
            throw new CollectionItemException("Current amount cannot be bigger than maximum");
        }

        foundItem.setCurrentAmount(currentAmount);
        foundItem.setMaxAmount(maxAmount);

        return itemMapper.mapToCollectionItemDTO(foundItem);
    }

    /**
     * @see CollectionItemService#submitItem(long, long, SubmittedItemDTO)
     */
    @Override
    @Transactional
    public CollectionItemDTO submitItem(long collectionId, long itemId, SubmittedItemDTO itemToSubmit) {
        Collection foundCollection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CollectionNotFoundException("Collection with id " + collectionId
                        + " not found in database"));
        CollectionItem foundItem = foundCollection.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new CollectionItemNotFoundException("Item with id " + itemId
                        + " not found in collection with id " + collectionId));

        itemToSubmit.setCollectionId(collectionId);
        itemToSubmit.setCollectionItemId(itemId);
        foundCollection.addSubmittedItem(submittedItemMapper.mapToSubmittedItem(itemToSubmit));
        return updateCollectionItem(collectionId, itemId,
                foundItem.getCurrentAmount() + itemToSubmit.getAmount(), foundItem.getMaxAmount());
    }

    /**
     * @see CollectionItemService#getSubmittedItems(long)
     */
    @Override
    public List<SubmittedItemDTO> getSubmittedItems(long collectionId) {
        Collection foundCollection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CollectionNotFoundException("Collection with id " + collectionId
                        + " not found in database"));
        return foundCollection.getSubmittedItems().stream()
                .map(submittedItemMapper::mapToSubmittedItemDTO)
                .collect(Collectors.toList());
    }

    /**
     * @see CollectionItemService#getSubmittedItemsForSpecificItem(long, long)
     */
    @Override
    public List<SubmittedItemDTO> getSubmittedItemsForSpecificItem(long collectionId, long itemId) {
        Collection foundCollection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CollectionNotFoundException("Collection with id " + collectionId
                        + " not found in database"));
        foundCollection.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new CollectionItemNotFoundException("Item with id " + itemId
                        + " not found in collection with id " + collectionId));
        return foundCollection.getSubmittedItems().stream()
                .filter(item -> item.getItemId().equals(itemId))
                .map(submittedItemMapper::mapToSubmittedItemDTO)
                .collect(Collectors.toList());
    }
}
