package pl.kucharski.Kordi.service.collection.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kucharski.Kordi.exception.CollectionItemException;
import pl.kucharski.Kordi.exception.CollectionItemNotFoundException;
import pl.kucharski.Kordi.exception.CollectionNotFoundException;
import pl.kucharski.Kordi.model.collection.Collection;
import pl.kucharski.Kordi.model.collection.CollectionDTO;
import pl.kucharski.Kordi.model.collection.CollectionMapper;
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
    private final CollectionMapper collectionMapper;
    private final SubmittedItemMapper submittedItemMapper;

    public CollectionItemServiceImpl(CollectionRepository collectionRepository, CollectionItemMapper itemMapper,
                                     CollectionMapper collectionMapper, SubmittedItemMapper submittedItemMapper) {
        this.collectionRepository = collectionRepository;
        this.itemMapper = itemMapper;
        this.collectionMapper = collectionMapper;
        this.submittedItemMapper = submittedItemMapper;
    }

    @Override
    @Transactional
    public void addCollectionItem(Long collectionId, CollectionItemDTO collectionItemDTO) {
        CollectionItem collectionItem = itemMapper.mapToCollectionItem(collectionItemDTO);
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(CollectionNotFoundException::new);
        collection.addItem(collectionItem);
    }

    @Override
    @Transactional
    public CollectionDTO updateCollectionItem(long collectionId, long itemId, int currentAmount, int maxAmount) {
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

        return collectionMapper.mapToCollectionDTO(collection);
    }

    @Override
    @Transactional
    public CollectionDTO submitItem(SubmittedItemDTO itemToSubmit) {
        Long collectionId = itemToSubmit.getCollectionId();
        Long itemId = itemToSubmit.getCollectionItemId();

        Collection foundCollection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CollectionNotFoundException("Collection with id " + collectionId
                        + " not found in database"));
        CollectionItem foundItem = foundCollection.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new CollectionItemNotFoundException("Item with id " + itemId
                        + " not found in collection with id " + collectionId));

        foundCollection.addSubmittedItem(submittedItemMapper.mapToSubmittedItem(itemToSubmit));
        return updateCollectionItem(collectionId, itemId,
                foundItem.getCurrentAmount() + itemToSubmit.getAmount(), foundItem.getMaxAmount());
    }

    @Override
    public List<SubmittedItemDTO> getSubmittedItems(long collectionId) {
        Collection foundCollection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CollectionNotFoundException("Collection with id " + collectionId
                        + " not found in database"));
        return foundCollection.getSubmittedItems().stream()
                .map(submittedItemMapper::mapToSubmittedItemDTO)
                .collect(Collectors.toList());
    }

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
