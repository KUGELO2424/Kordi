package pl.kucharski.Kordi.service.collection.impl;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.kucharski.Kordi.aop.IsCollectionOwner;
import pl.kucharski.Kordi.exception.CollectionItemException;
import pl.kucharski.Kordi.exception.CollectionItemNotFoundException;
import pl.kucharski.Kordi.exception.CollectionNotFoundException;
import pl.kucharski.Kordi.exception.UserNotFoundException;
import pl.kucharski.Kordi.model.collection.Collection;
import pl.kucharski.Kordi.model.collection_item.CollectionItem;
import pl.kucharski.Kordi.model.collection_item.CollectionItemDTO;
import pl.kucharski.Kordi.model.collection_item.CollectionItemMapper;
import pl.kucharski.Kordi.model.collection_submitted_item.SubmittedItem;
import pl.kucharski.Kordi.model.collection_submitted_item.SubmittedItemDTO;
import pl.kucharski.Kordi.model.collection_submitted_item.SubmittedItemMapper;
import pl.kucharski.Kordi.model.user.User;
import pl.kucharski.Kordi.repository.CollectionRepository;
import pl.kucharski.Kordi.repository.SubmittedItemRepository;
import pl.kucharski.Kordi.repository.UserRepository;
import pl.kucharski.Kordi.service.collection.CollectionItemService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static pl.kucharski.Kordi.config.ErrorCodes.*;

@Service
@Transactional(readOnly = true)
public class CollectionItemServiceImpl implements CollectionItemService {

    private final CollectionRepository collectionRepository;
    private final SubmittedItemRepository submittedItemRepository;
    private final UserRepository userRepository;
    private final CollectionItemMapper itemMapper;
    private final SubmittedItemMapper submittedItemMapper;
    private final CollectionHelper collectionHelper;

    public CollectionItemServiceImpl(CollectionRepository collectionRepository, SubmittedItemRepository submittedItemRepository, UserRepository userRepository, CollectionItemMapper itemMapper,
                                     SubmittedItemMapper submittedItemMapper, CollectionHelper collectionHelper) {
        this.collectionRepository = collectionRepository;
        this.submittedItemRepository = submittedItemRepository;
        this.userRepository = userRepository;
        this.itemMapper = itemMapper;
        this.submittedItemMapper = submittedItemMapper;
        this.collectionHelper = collectionHelper;
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

        collectionHelper.updateStatus(collection);

        return itemMapper.mapToCollectionItemDTO(foundItem);
    }

    /**
     * @see CollectionItemService#submitItems(long, List)
     */
    @Override
    @Transactional
    public List<CollectionItemDTO> submitItems(long collectionId, List<SubmittedItemDTO> items) {
        List<CollectionItemDTO> submittedItems = new ArrayList<>();
        for (SubmittedItemDTO itemToSubmit : items) {
            CollectionItemDTO submittedItem = submitItem(collectionId, itemToSubmit.getCollectionItemId(), itemToSubmit);
            submittedItems.add(submittedItem);
        }
        return submittedItems;
    }

    /**
     * @see CollectionItemService#submitItem(long, long, SubmittedItemDTO)
     */
    @Override
    @Transactional
    public CollectionItemDTO submitItem(long collectionId, long itemId, SubmittedItemDTO itemToSubmit) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username == null) {
            throw new UserNotFoundException(USER_NOT_FOUND);
        }
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        Collection foundCollection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CollectionNotFoundException(COLLECTION_NOT_FOUND));
        CollectionItem foundItem = foundCollection.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new CollectionItemNotFoundException(COLLECTION_ITEM_NOT_FOUND));

        itemToSubmit.setUserId(user.getId());
        itemToSubmit.setUsername(username);
        itemToSubmit.setCollectionId(collectionId);
        itemToSubmit.setCollectionItemId(itemId);
        foundCollection.addSubmittedItem(submittedItemMapper.mapToSubmittedItem(itemToSubmit));
        foundCollection.setDonates(foundCollection.getDonates() + 1);
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
                .sorted((o1, o2) -> o2.getSubmitTime().compareTo(o1.getSubmitTime()))
                .collect(Collectors.toList());
    }

    /**
     * @see CollectionItemService#getSubmittedItemsForUser(String, Pageable) 
     */
    @Override
    public Page<SubmittedItemDTO> getSubmittedItemsForUser(String username, Pageable pageable) {
        Page<SubmittedItem> submittedItemsPage;
        try {
            submittedItemsPage = submittedItemRepository.findByUserUsername(username, pageable);
        } catch (InvalidDataAccessApiUsageException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    ex.getMessage());
        }
        if (submittedItemsPage.isEmpty()) {
            return Page.empty();
        }

        List<SubmittedItemDTO> submittedItems = submittedItemsPage.stream().map(submittedItemMapper::mapToSubmittedItemDTO).toList();
        return new PageImpl<>(submittedItems, pageable, submittedItemsPage.getTotalElements());
    }

    /**
     * @see CollectionItemService#getLastSubmittedItems(long, int)
     */
    @Override
    public List<SubmittedItemDTO> getLastSubmittedItems(long collectionId, int numberOfSubmittedItems) {
        Collection foundCollection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CollectionNotFoundException(COLLECTION_NOT_FOUND));

        return foundCollection.getSubmittedItems().stream()
                .map(submittedItemMapper::mapToSubmittedItemDTO)
                .sorted((o1, o2) -> o2.getSubmitTime().compareTo(o1.getSubmitTime()))
                .limit(numberOfSubmittedItems)
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
