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
import pl.kucharski.Kordi.enums.ItemCategory;
import pl.kucharski.Kordi.exception.CollectionNotFoundException;
import pl.kucharski.Kordi.exception.UserNotFoundException;
import pl.kucharski.Kordi.model.collection.Collection;
import pl.kucharski.Kordi.model.collection.CollectionDTO;
import pl.kucharski.Kordi.model.collection.CollectionMapper;
import pl.kucharski.Kordi.model.user.User;
import pl.kucharski.Kordi.repository.CollectionRepository;
import pl.kucharski.Kordi.repository.UserRepository;
import pl.kucharski.Kordi.service.collection.CollectionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static pl.kucharski.Kordi.config.ErrorCodes.COLLECTION_END_DATE_INVALID;
import static pl.kucharski.Kordi.config.ErrorCodes.COLLECTION_NOT_FOUND;
import static pl.kucharski.Kordi.config.ErrorCodes.USER_NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class CollectionServiceImpl implements CollectionService {

    private final CollectionRepository collectionRepository;
    private final UserRepository userRepository;
    private final CollectionMapper collectionMapper;

    public CollectionServiceImpl(CollectionRepository collectionRepository, UserRepository userRepository, CollectionMapper collectionMapper) {
        this.collectionRepository = collectionRepository;
        this.userRepository = userRepository;
        this.collectionMapper = collectionMapper;
    }

    /**
     * @see CollectionService#getCollectionById(long)
     */
    @Override
    public CollectionDTO getCollectionById(long id) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new CollectionNotFoundException(COLLECTION_NOT_FOUND));

        return collectionMapper.mapToCollectionDTO(collection);
    }

    /**
     * @see CollectionService#getCollectionsByUser(String, Pageable)
     */
    @Override
    public List<CollectionDTO> getCollectionsByUser(String username, Pageable pageable) {
        List<Collection> collections = collectionRepository.findByUserUsername(username, pageable);
        return collections
                .stream()
                .map(collectionMapper::mapToCollectionDTO)
                .collect(Collectors.toList());
    }

    /**
     * @see CollectionService#getCollectionsWithFiltering(String, String, String, String, List, Pageable)
     */
    @Override
    public Page<CollectionDTO> getCollectionsWithFiltering(String title, String city, String street, String itemName,
                                                           List<ItemCategory> categories, Pageable pageable) {

        Page<Collection> collectionsPage;
        try {
            collectionsPage = collectionRepository.findWithFiltering(title, city, street, itemName, categories.size(),
                    categories, pageable);
        } catch (InvalidDataAccessApiUsageException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    ex.getMessage());
        }
        if (collectionsPage == null) {
            return Page.empty();
        }
        List<CollectionDTO> collections = collectionsPage.stream().map(collectionMapper::mapToCollectionDTO).toList();

        return new PageImpl<>(collections, pageable, collectionsPage.getTotalElements());
    }

    /**
     * @see CollectionService#saveCollection(CollectionDTO)
     */
    @Override
    @Transactional
    public CollectionDTO saveCollection(CollectionDTO collectionDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        if (collectionDTO.getEndTime() != null && collectionDTO.getEndTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(COLLECTION_END_DATE_INVALID);
        }
        collectionDTO.setUserId(user.getId());
        Collection collection = collectionMapper.mapToCollection(collectionDTO);
        Collection savedCollection = collectionRepository.save(collection);
        return collectionMapper.mapToCollectionDTO(savedCollection);
    }

    /**
     * @see CollectionService#updateCollection(long, String, String, LocalDateTime)
     */
    @Override
    @Transactional
    @IsCollectionOwner
    public CollectionDTO updateCollection(long collectionId, String title, String description, LocalDateTime endTime) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CollectionNotFoundException(COLLECTION_NOT_FOUND));
        if (title != null && !title.isEmpty()) {
            collection.setTitle(title);
        }
        if (description != null && !description.isEmpty()) {
            collection.setDescription(description);
        }
        if (endTime != null) {
            if (endTime.isBefore(LocalDateTime.now())) {
                collection.setEndTime(LocalDateTime.now());
            } else {
                collection.setEndTime(endTime);
            }
        }
        return collectionMapper.mapToCollectionDTO(collection);
    }
}
