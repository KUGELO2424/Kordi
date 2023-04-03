package pl.kucharski.Kordi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
import pl.kucharski.Kordi.enums.CollectionStatus;
import pl.kucharski.Kordi.enums.ItemCategory;
import pl.kucharski.Kordi.exception.AddressAlreadyExistsInCollectionException;
import pl.kucharski.Kordi.exception.CollectionNotFoundException;
import pl.kucharski.Kordi.exception.UserNotFoundException;
import pl.kucharski.Kordi.model.address.AddressDTO;
import pl.kucharski.Kordi.model.collection.CollectionDTO;
import pl.kucharski.Kordi.model.collection.CollectionUpdateDTO;
import pl.kucharski.Kordi.service.collection.CollectionAddressService;
import pl.kucharski.Kordi.service.collection.CollectionService;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Collection controller responsible for basic collection management
 *
 * @author Grzegorz Kucharski gelo2424@wp.pl
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

    @Operation(summary = "Get all collections of user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "list of user collections"),
    })
    @GetMapping("/users/{username}/collections")
    ResponseEntity<List<CollectionDTO>> getAllUserCollections(
            @Parameter(description = "username of user") @PathVariable("username") String username,
            @Parameter(description = "page number") @RequestParam(value = "pageNo",
                    defaultValue = PaginationConstants.DEFAULT_PAGE_NUMBER,
                    required = false) int pageNo,
            @Parameter(description = "page size") @RequestParam(value = "pageSize",
                    defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE,
                    required = false) int pageSize) {
        log.info("Request to get all collections for user {}", username);
        return ResponseEntity.ok(collectionService
                .getCollectionsByUser(username, PageRequest.of(pageNo, pageSize, Sort.by("startTime").descending())));
    }

    @Operation(summary = "Get collection by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "collection"),
            @ApiResponse(responseCode = "404", description = "collection not found", content = @Content),
    })
    @GetMapping("/collections/{collectionId}")
    ResponseEntity<CollectionDTO> getCollectionById(@Parameter(description = "id of collection") @PathVariable("collectionId") Long collectionId) {
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

    @Operation(summary = "Get collections with filtering",
            description = "Get collections with filtering. Default value for params is empty string, then all collections match")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "page of collection"),
    })
    @GetMapping("/collections")
    ResponseEntity<Page<CollectionDTO>> searchCollections(
            @Parameter(description = "title of collection")
            @RequestParam(value = "title", defaultValue = "", required = false) String title,
            @Parameter(description = "city from collection")
            @RequestParam(value = "city", defaultValue = "", required = false) String city,
            @Parameter(description = "street from collection")
            @RequestParam(value = "street", defaultValue = "", required = false) String street,
            @Parameter(description = "name of item from collection")
            @RequestParam(value = "itemName", defaultValue = "", required = false) String itemName,
            @Parameter(description = "list of categories")
            @RequestParam(value = "categories", defaultValue = "", required = false) List<ItemCategory> categories,
            @Parameter(description = "collection status")
            @RequestParam(value = "status", defaultValue = "IN_PROGRESS", required = false) CollectionStatus status,
            @Parameter(description = "page number")
            @RequestParam(value = "pageNo", defaultValue = PaginationConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @Parameter(description = "page size")
            @RequestParam(value = "pageSize", defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @Parameter(description = "sort by this field")
            @RequestParam(value = "sortBy", defaultValue = PaginationConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @Parameter(description = "sort direction")
            @RequestParam(value = "sortDirection", defaultValue = PaginationConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDirection) {
        log.info("Request to get collections with search params, title:{}, city:{}, street:{}, itemName:{}, status:{}, categories:{}",
                title, city, street, itemName, status, categories);
        Pageable pageable = PaginationConstants.getPageable(pageNo, pageSize, sortBy, sortDirection);
        return ResponseEntity
                .ok(collectionService.getCollectionsWithFiltering(title, city, street, itemName, status, categories, pageable));
    }

    @Operation(summary = "Save new collection")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "saved collection"),
            @ApiResponse(responseCode = "404", description = "user not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "itemType or itemCategory is wrong", content = @Content),
            @ApiResponse(responseCode = "400", description = "title is empty", content = @Content),
    })
    @PostMapping("/collections")
    ResponseEntity<CollectionDTO> saveCollection(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "collection to save")
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
     * @param collectionToUpdate new values for collection. If some value is empty, then old value stay
     * @return updated collection<br>
     * status 404 if collection not found<br>
     * status 403 if logged user is not an owner of collection
     */
    @Operation(summary = "Update existing collection")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "updated collection"),
            @ApiResponse(responseCode = "404", description = "collection not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "user not an owner of collection", content = @Content),
    })
    @PatchMapping("/collections")
    @CrossOrigin("http://localhost:4200")
    ResponseEntity<CollectionDTO> updateCollection(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "collection to update. If some value is empty, then no value update")
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

    @Operation(summary = "add new address to collection")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "address added"),
            @ApiResponse(responseCode = "404", description = "collection not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "user not an owner of collection", content = @Content),
    })
    @PostMapping("/collections/{collectionId}/addresses")
    ResponseEntity<Map<String, String>> addAddressToCollection(@Parameter(description = "id of collection") @PathVariable long collectionId,
                                                               @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "address to add")
                                                               @RequestBody AddressDTO address) {
        try {
            log.info("Request to add new address: {} to collection with id {}", address, collectionId);
            addressService.addCollectionAddress(collectionId, address);
            return ResponseEntity.ok(Collections.singletonMap("status", "New address added to collection with id " + collectionId));
        } catch (CollectionNotFoundException ex) {
            log.warn("Collection with id {} not found", collectionId);
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    ex.getMessage());
        } catch (AddressAlreadyExistsInCollectionException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    ex.getMessage());
        }
    }

    @Operation(summary = "remove address from collection")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "address removed"),
            @ApiResponse(responseCode = "404", description = "collection not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "user not an owner of collection", content = @Content),
    })
    @DeleteMapping("/collections/{collectionId}/addresses")
    ResponseEntity<Map<String, String>> removeAddressFromCollection(@Parameter(description = "id of collection") @PathVariable long collectionId,
                                                                    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "address to delete")
                                                                    @RequestBody AddressDTO address) {
        try {
            log.info("Request to remove address: {} from collection with id {}", address, collectionId);
            addressService.removeCollectionAddress(collectionId, address);
            return ResponseEntity.ok(Collections.singletonMap("status", "Address removed from collection with id " + collectionId));
        } catch (CollectionNotFoundException ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    ex.getMessage());
        }
    }

}
