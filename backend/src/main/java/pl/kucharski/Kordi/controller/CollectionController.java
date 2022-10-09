package pl.kucharski.Kordi.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pl.kucharski.Kordi.config.PaginationConstants;
import pl.kucharski.Kordi.exception.CollectionNotFoundException;
import pl.kucharski.Kordi.exception.UserNotFoundException;
import pl.kucharski.Kordi.model.collection.CollectionDTO;
import pl.kucharski.Kordi.model.collection.CollectionUpdateDTO;
import pl.kucharski.Kordi.service.collection.CollectionService;

import javax.validation.Valid;
import java.net.URI;

@RestController
public class CollectionController {

    private final CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    /**
     * Get all collections of user
     * @param username of user
     * @param pageNo number of page, default value is 0
     * @param pageSize size of page, default value is 10
     *
     * @return list of collections of given user
     */
    @GetMapping("/user/{username}/collections")
    ResponseEntity<?> getAllUserCollections(
            @PathVariable("username") String username,
            @RequestParam(value = "pageNo",
                    defaultValue = PaginationConstants.DEFAULT_PAGE_NUMBER,
                    required = false) int pageNo,
            @RequestParam(value = "pageSize",
                    defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE,
                    required = false) int pageSize) {
        return ResponseEntity.ok(collectionService.getCollectionsByUser(username, PageRequest.of(pageNo, pageSize)));
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
            CollectionDTO collection = collectionService.getCollectionById(collectionId);
            return ResponseEntity.ok(collection);
        } catch (CollectionNotFoundException ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Collection with id " + collectionId + " not found");
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
            @RequestParam(value = "pageNo",
                defaultValue = PaginationConstants.DEFAULT_PAGE_NUMBER,
                required = false) int pageNo,
            @RequestParam(value = "pageSize",
                defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE,
                required = false) int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return ResponseEntity.ok(collectionService.getCollectionsWithFiltering(title, city, street, itemName, pageable));
    }

    /**
     * Save new collection
     * @param collection to add
     *
     * @return created collection with ids<br>
     * status 404 if user not found<br>
     * status 400 if itemType is wrong<br>
     * status 400 if title is empty
     */
    @PostMapping("/collections")
    ResponseEntity<?> saveCollection(
            @RequestBody @Valid CollectionDTO collection) {
        try {
            CollectionDTO savedCollection = collectionService.saveCollection(collection);
            return ResponseEntity.created(URI.create("/collections/" + savedCollection.getId())).body(savedCollection);
        } catch (UserNotFoundException ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User with id " + collection.getUserId() + " not found");
        }
    }

    /**
     * Update existing collection
     * @param collectionToUpdate new values for collection. If some value is empty, then old value stay
     *
     * @return updated collection<br>
     * status 404 if collection not found<br>
     */
    @PatchMapping("/collections")
    ResponseEntity<?> updateCollection(
            @RequestBody CollectionUpdateDTO collectionToUpdate) {
        try {
            CollectionDTO updatedCollection = collectionService.updateCollection(
                    collectionToUpdate.getId(),
                    collectionToUpdate.getTitle(),
                    collectionToUpdate.getDescription(),
                    collectionToUpdate.getEndTime());
            return ResponseEntity.ok(updatedCollection);
        } catch (CollectionNotFoundException ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Collection with id " + collectionToUpdate.getId() + " not found");
        }
    }

}
