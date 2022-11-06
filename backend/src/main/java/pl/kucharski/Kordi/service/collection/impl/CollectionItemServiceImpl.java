package pl.kucharski.Kordi.service.collection.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kucharski.Kordi.aop.IsCollectionOwner;
import pl.kucharski.Kordi.exception.CollectionItemException;
import pl.kucharski.Kordi.exception.CollectionItemNotFoundException;
import pl.kucharski.Kordi.exception.CollectionNotFoundException;
import pl.kucharski.Kordi.exception.UserNotFoundException;
import pl.kucharski.Kordi.model.collection.Collection;
import pl.kucharski.Kordi.model.collection_item.CollectionItem;
import pl.kucharski.Kordi.model.collection_item.CollectionItemDTO;
import pl.kucharski.Kordi.model.collection_item.CollectionItemMapper;
import pl.kucharski.Kordi.model.collection_submitted_item.SubmittedItemDTO;
import pl.kucharski.Kordi.model.collection_submitted_item.SubmittedItemMapper;
import pl.kucharski.Kordi.model.user.User;
import pl.kucharski.Kordi.repository.CollectionRepository;
import pl.kucharski.Kordi.repository.UserRepository;
import pl.kucharski.Kordi.service.collection.CollectionItemService;

import java.util.List;
import java.util.stream.Collectors;

import static pl.kucharski.Kordi.config.ErrorCodes.COLLECTION_ITEM_CURRENT_BIGGER_THAN_MAX;
import static pl.kucharski.Kordi.config.ErrorCodes.COLLECTION_ITEM_NOT_FOUND;
import static pl.kucharski.Kordi.config.ErrorCodes.COLLECTION_NOT_FOUND;
import static pl.kucharski.Kordi.config.ErrorCodes.USER_NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class CollectionItemServiceImpl implements CollectionItemService {

    private final CollectionRepository collectionRepository;
    private final UserRepository userRepository;
    private final CollectionItemMapper itemMapper;
    private final SubmittedItemMapper submittedItemMapper;

    public CollectionItemServiceImpl(CollectionRepository collectionRepository, UserRepository userRepository, CollectionItemMapper itemMapper,
                                     SubmittedItemMapper submittedItemMapper) {
        this.collectionRepository = collectionRepository;
        this.userRepository = userRepository;
        this.itemMapper = itemMapper;
        this.submittedItemMapper = submittedItemMapper;
    }

    /**
     * @see CollectionItemService#addCollectionItem(Long, CollectionItemDTO)
     */
    @Override
    @Transactional
    @IsCollectionOwner
    public void addCollectionItem(Long collectionId, CollectionItemDTO collectionItemDTO) {
        CollectionItem collectionItem = itemMapper.mapToCollectionItem(collectionItemDTO);
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CollectionNotFoundException(COLLECTION_NOT_FOUND));
        collection.addItem(collectionItem);
    }

    /**
     * @see CollectionItemService#updateCollectionItem(long, long, int, int)
     */
    @Override
    @Transactional
    public CollectionItemDTO updateCollectionItem(long collectionId, long itemId, int currentAmount, int maxAmount) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CollectionNotFoundException(COLLECTION_NOT_FOUND));
        CollectionItem foundItem = collection.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new CollectionItemNotFoundException(COLLECTION_ITEM_NOT_FOUND));

        if (maxAmount < currentAmount) {
            throw new CollectionItemException(COLLECTION_ITEM_CURRENT_BIGGER_THAN_MAX);
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
                .orElseThrow(() -> new CollectionNotFoundException(COLLECTION_NOT_FOUND));
        userRepository.findById(itemToSubmit.getUserId())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        CollectionItem foundItem = foundCollection.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new CollectionItemNotFoundException(COLLECTION_ITEM_NOT_FOUND));

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
                .orElseThrow(() -> new CollectionNotFoundException(COLLECTION_NOT_FOUND));
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
                .orElseThrow(() -> new CollectionNotFoundException(COLLECTION_NOT_FOUND));
        foundCollection.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new CollectionItemNotFoundException(COLLECTION_ITEM_NOT_FOUND));
        return foundCollection.getSubmittedItems().stream()
                .filter(item -> item.getItemId().equals(itemId))
                .map(submittedItemMapper::mapToSubmittedItemDTO)
                .collect(Collectors.toList());
    }
}
