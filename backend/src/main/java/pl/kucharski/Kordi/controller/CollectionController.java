package pl.kucharski.Kordi.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pl.kucharski.Kordi.config.PaginationConstants;
import pl.kucharski.Kordi.enums.ItemCategory;
import pl.kucharski.Kordi.exception.CollectionNotFoundException;
import pl.kucharski.Kordi.exception.UserNotFoundException;
import pl.kucharski.Kordi.model.address.AddressDTO;
import pl.kucharski.Kordi.model.collection.CollectionDTO;
import pl.kucharski.Kordi.model.collection.CollectionUpdateDTO;
import pl.kucharski.Kordi.service.collection.CollectionAddressService;
import pl.kucharski.Kordi.service.collection.CollectionService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

/**
 * Collection controller responsible for basic collection management
 *
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */
@Slf4j
@RestController
public class CollectionController {

    private final CollectionService collectionService;
    private final CollectionAddressService addressService;

    public CollectionController(CollectionService collectionService, CollectionAddressService addressService) {
        this.collectionService = collectionService;
        this.addressService = addressService;
    }

    /**
     * Get all collections of user
     * @param username of user
     * @param pageNo number of page, default value is 0
     * @param pageSize size of page, default value is 10
     *
     * @return list of collections of given user
     */
    @GetMapping("/users/{username}/collections")
    ResponseEntity<?> getAllUserCollections(
            @PathVariable("username") String username,
            @RequestParam(value = "pageNo",
                    defaultValue = PaginationConstants.DEFAULT_PAGE_NUMBER,
                    required = false) int pageNo,
            @RequestParam(value = "pageSize",
                    defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE,
                    required = false) int pageSize) {
        log.info("Request to get all collections for user {}", username);
        return ResponseEntity.ok(collectionService
                .getCollectionsByUser(username, PageRequest.of(pageNo, pageSize, Sort.by("startTime").descending())));
    }

    /**
     * Get collection by id
     * @param collectionId id of collection
     *
     * @return collection if found<br>
     * status 404 - if collection not found
     */
    @GetMapping("/collections/{collectionId}")
    ResponseEntity<?> getCollectionById(@PathVariable("collectionId") Long collectionId) {
        try {
            log.info("Request to get collection by id, collectionId: {}", collectionId);
            CollectionDTO collection = collectionService.getCollectionById(collectionId);
            log.info("Returning collection: {}", collection.getId());
            return ResponseEntity.ok(collection);
        } catch (CollectionNotFoundException ex) {
            log.warn("Collection with id {} not found", collectionId);
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    ex.getMessage());
        }
    }

    /**
     * Get collections with filtering. Default value for params is empty string, then all collections match.
     * @param title of collection
     * @param city of address in collection
     * @param street of address in collection
     * @param itemName of item in collection
     * @param pageNo number of page, default value is 0
     * @param pageSize size of page, default value is 10
     *
     * @return list of found collections
     */
    @GetMapping("/collections")
    ResponseEntity<?> searchCollections(
            @RequestParam(value = "title", defaultValue = "", required = false) String title,
            @RequestParam(value = "city", defaultValue = "", required = false) String city,
            @RequestParam(value = "street", defaultValue = "", required = false) String street,
            @RequestParam(value = "itemName", defaultValue = "", required = false) String itemName,
            @RequestParam(value = "categories", defaultValue = "", required = false) List<ItemCategory> categories,
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
        log.info("Request to get collections with search params, title:{}, city:{}, street:{}, itemName:{}, categories:{}",
                title, city, street, itemName, categories);
        Pageable pageable = PaginationConstants.getPageable(pageNo, pageSize, sortBy, sortDirection);
        return ResponseEntity
                .ok(collectionService.getCollectionsWithFiltering(title, city, street, itemName, categories, pageable));
    }

    /**
     * Save new collection
     * @param collection to add
     *
     * @return created collection with ids<br>
     * status 404 if user not found<br>
     * status 400 if itemType or itemCategory is wrong<br>
     * status 400 if title is empty
     */
    @PostMapping("/collections")
    ResponseEntity<?> saveCollection(
            @RequestBody @Valid CollectionDTO collection) {
        try {
            log.info("Request to save collection: {}", collection.getTitle());
            CollectionDTO savedCollection = collectionService.saveCollection(collection);
            return ResponseEntity.created(URI.create("/collections/" + savedCollection.getId())).body(savedCollection);
        } catch (UserNotFoundException ex) {
            log.warn("Logged user not found in database");
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    ex.getMessage());
        }
    }

    /**
     * Update existing collection
     * @param collectionToUpdate new values for collection. If some value is empty, then old value stay
     *
     * @return updated collection<br>
     * status 404 if collection not found<br>
     * status 403 if logged user is not an owner of collection
     */
    @CrossOrigin("${allowed.origins}")
    @PatchMapping("/collections")
    ResponseEntity<?> updateCollection(
            @RequestBody CollectionUpdateDTO collectionToUpdate) {
        try {
            log.info("Request to update collection: {}", collectionToUpdate);
            CollectionDTO updatedCollection = collectionService.updateCollection(
                    collectionToUpdate.getId(),
                    collectionToUpdate.getTitle(),
                    collectionToUpdate.getDescription(),
                    collectionToUpdate.getEndTime());
            return ResponseEntity.ok(updatedCollection);
        } catch (CollectionNotFoundException ex) {
            log.warn("Collection with id {} not found", collectionToUpdate.getId());
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    ex.getMessage());
        }
    }

    /**
     * Add new address to collection
     * @param collectionId id of collection where you want to add new address
     * @param address new address to add
     *
     * @return message if address added<br>
     * status 404 if collection not found<br>
     * status 403 if logged user is not an owner of collection<br>
     */
    @PostMapping("/collections/{collectionId}/addresses")
    ResponseEntity<?> addAddressToCollection(@PathVariable long collectionId, @RequestBody AddressDTO address) {
        try {
            log.info("Request to add new address: {} to collection with id {}", address, collectionId);
            addressService.addCollectionAddress(collectionId, address);
            return ResponseEntity.ok("New address added to collection with id " + collectionId);
        } catch (CollectionNotFoundException ex) {
            log.warn("Collection with id {} not found", collectionId);
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    ex.getMessage());
        }
    }

    /**
     * Add new address to collection
     * @param collectionId id of collection where you want to add new address
     * @param address new address to add
     *
     * @return message if address added<br>
     * status 404 if collection not found<br>
     * status 403 if logged user is not an owner of collection<br>
     */
    @DeleteMapping("/collections/{collectionId}/addresses")
    ResponseEntity<?> removeAddressFromCollection(@PathVariable long collectionId, @RequestBody AddressDTO address) {
        try {
            log.info("Request to remove address: {} from collection with id {}", address, collectionId);
            addressService.removeCollectionAddress(collectionId, address);
            return ResponseEntity.ok("Address removed from collection with id " + collectionId);
        } catch (CollectionNotFoundException ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    ex.getMessage());
        }
    }

}
