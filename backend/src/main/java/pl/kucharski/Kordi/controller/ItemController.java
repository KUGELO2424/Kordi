package pl.kucharski.Kordi.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pl.kucharski.Kordi.config.PaginationConstants;
import pl.kucharski.Kordi.enums.ItemCategory;
import pl.kucharski.Kordi.exception.CollectionItemException;
import pl.kucharski.Kordi.exception.CollectionItemNotFoundException;
import pl.kucharski.Kordi.exception.CollectionNotFoundException;
import pl.kucharski.Kordi.exception.UserNotFoundException;
import pl.kucharski.Kordi.model.collection_item.CollectionItemDTO;
import pl.kucharski.Kordi.model.collection_item.CollectionItemUpdateDTO;
import pl.kucharski.Kordi.model.collection_submitted_item.SubmittedItemDTO;
import pl.kucharski.Kordi.service.collection.CollectionItemService;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

/**
 * Collection item controller responsible for item management
 *
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */
@Slf4j
@RestController
@RequestMapping("/collections")
public class ItemController {

    private final CollectionItemService itemService;

    public ItemController(CollectionItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * Add new item to collection
     * @param collectionId id of collection which you want to add new item
     * @param item object of item to add
     *
     * @return created new item
     * status 404 if collection not found
     * status 400 if itemType or itemCategory is wrong<br>
     */
    @PostMapping("/{collectionId}")
    ResponseEntity<?> addNewItemToCollection(@PathVariable("collectionId") Long collectionId,
                                             @RequestBody @Valid CollectionItemDTO item) {
        try {
            log.info("Request to add item {} to collection with id {}", item, collectionId);
            itemService.addCollectionItem(collectionId, item);
            return ResponseEntity.ok().body("Item added to collection with id " + collectionId);
        } catch (CollectionNotFoundException ex) {
            log.warn("Collection with id {} not found", collectionId);
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    ex.getMessage());
        }
    }

    /**
     * Update existing item in collection
     * @param collectionId id of collection in which you look for a item
     * @param itemId id of searched item
     * @param item item with new values
     *
     * @return updated item
     * status 404 if collection not found<br>
     * status 404 if item not found<br>
     * status 400 if new values are incorrect
     */
    @PatchMapping("/{collectionId}/items/{itemId}")
    ResponseEntity<?> updateCollectionItem(@PathVariable("collectionId") Long collectionId,
                                           @PathVariable("itemId") Long itemId,
                                           @RequestBody @Valid CollectionItemUpdateDTO item) {
        try {
            log.info("Request to update item {} with id {} from collection with id {}", item, itemId, collectionId);
            CollectionItemDTO updatedItem =
                    itemService.updateCollectionItem(collectionId, itemId, item.getCurrentAmount(), item.getMaxAmount());
            return ResponseEntity.ok(updatedItem);
        } catch (CollectionItemException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (CollectionNotFoundException | CollectionItemNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    /**
     * Submit items
     * @param collectionId id of collection for which you want to submit item
     * @param items items to submit
     *
     * @return updated item after submitting
     * status 404 if collection not found<br>
     * status 404 if item not found<br>
     * status 400 if new values are incorrect
     */
    @PostMapping("/{collectionId}/items/submit")
    ResponseEntity<?> submitItem(@PathVariable("collectionId") Long collectionId,
                                 @RequestBody List<SubmittedItemDTO> items) {
        try {
            log.info("Request to submit items {}", items);
            List<CollectionItemDTO> updatedItem = itemService.submitItems(collectionId, items);
            return ResponseEntity.ok(updatedItem);
        } catch (CollectionItemException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (CollectionNotFoundException | CollectionItemNotFoundException | UserNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    /**
     * Get submitted items from collection
     * @param collectionId id of collection with submitted items
     * @param numberOfSubmittedItems number of last submitted items to get from collection
     * @return list of submitted items
     * status 404 if collection not found<br>
     */
    @GetMapping("/{collectionId}/submittedItems")
    ResponseEntity<?> getSubmittedItems(@PathVariable("collectionId") Long collectionId,
                                        @RequestParam(value = "numberOfSubmittedItems", required = false) Integer numberOfSubmittedItems) {
        try {
            log.info("Request to get submitted items from collection with id {}", collectionId);
            List<SubmittedItemDTO> submittedItems;
            if (numberOfSubmittedItems == null) {
                submittedItems = itemService.getSubmittedItems(collectionId);
            } else {
                submittedItems = itemService.getLastSubmittedItems(collectionId, numberOfSubmittedItems);
            }
            return ResponseEntity.ok(submittedItems);
        } catch (CollectionNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    /**
     * Get submitted items for user
     * @param username username of user
     * @return list of submitted items
     */
    @GetMapping("/submittedItems/{username}")
    ResponseEntity<?> getSubmittedItems(@PathVariable("username") String username,
                                        @RequestParam(value = "pageNo",
                                                defaultValue = PaginationConstants.DEFAULT_PAGE_NUMBER,
                                                required = false) int pageNo,
                                        @RequestParam(value = "pageSize",
                                                defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE,
                                                required = false) int pageSize,
                                        @RequestParam(value = "sortBy",
                                                defaultValue = PaginationConstants.DEFAULT_SORT_BY,
                                                required = false) String sortBy,
                                        @RequestParam(value = "sortDirection",
                                                defaultValue = PaginationConstants.DEFAULT_SORT_DIRECTION,
                                                required = false) String sortDirection) {
        log.info("Request to get submitted items for user {}", username);
        Pageable pageable = PaginationConstants.getPageable(pageNo, pageSize, sortBy, sortDirection);
        Page<SubmittedItemDTO> submittedItems = itemService.getSubmittedItemsForUser(username, pageable);
        return ResponseEntity.ok(submittedItems);
    }

    /**
     * Get all submitted items from collection for specific item
     * @param collectionId id of collection with submitted items
     * @param itemId id of item which you want get submitted items
     *
     * @return list of submitted items for specific item
     * status 404 if collection not found<br>
     * status 404 if item not found<br>
     */
    @GetMapping("/{collectionId}/items/{itemId}/submittedItems")
    ResponseEntity<?> getSubmittedItems(@PathVariable("collectionId") Long collectionId,
                                        @PathVariable("itemId") Long itemId) {
        try {
            log.info("Request to get submitted items from collection with id {} for item with id {}", collectionId, itemId);
            List<SubmittedItemDTO> submittedItems = itemService.getSubmittedItemsForSpecificItem(collectionId, itemId);
            return ResponseEntity.ok(submittedItems);
        } catch (CollectionNotFoundException | CollectionItemNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    /**
     * Get all available item categories
     *
     * @return list of available item categories
     */
    @GetMapping("/categories")
    ResponseEntity<?> getAllCategories() {
        log.info("Request to get all categories");
        return ResponseEntity.ok(Arrays.stream(ItemCategory.values()).toList());
    }

}
