package pl.kucharski.Kordi.service.collection.impl;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kucharski.Kordi.exception.CollectionNotFoundException;
import pl.kucharski.Kordi.model.collection.Collection;
import pl.kucharski.Kordi.model.collection.CollectionDTO;
import pl.kucharski.Kordi.model.collection.CollectionMapper;
import pl.kucharski.Kordi.repository.CollectionRepository;
import pl.kucharski.Kordi.service.collection.CollectionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CollectionServiceImpl implements CollectionService {

    private final CollectionRepository collectionRepository;
    private final CollectionMapper collectionMapper;

    public CollectionServiceImpl(CollectionRepository collectionRepository, CollectionMapper collectionMapper) {
        this.collectionRepository = collectionRepository;
        this.collectionMapper = collectionMapper;
    }

    @Override
    public CollectionDTO getCollectionById(long id) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new CollectionNotFoundException("Collection not found in database"));

        return collectionMapper.mapToCollectionDTO(collection);
    }

    @Override
    public List<CollectionDTO> getCollectionsByUser(String username, Pageable pageable) {
        List<Collection> collections = collectionRepository.findByUserUsername(username, pageable);
        return collections
                .stream()
                .map(collectionMapper::mapToCollectionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CollectionDTO> getCollectionsWithFiltering(String title, String city, String street, String itemName, Pageable pageable) {
        List<Collection> collections;
        if (itemName.isEmpty() && city.isEmpty() && street.isEmpty()) {
            collections = collectionRepository.findByTitleContaining(title, pageable);
        } else if (itemName.isEmpty()) {
            collections = collectionRepository.findByTitleAndAddress(title, city, street, pageable);
        } else {
            collections = collectionRepository.findByTitleAndAddressAndItem(title, city, street, itemName, pageable);
        }

        return collections
                .stream()
                .map(collectionMapper::mapToCollectionDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CollectionDTO saveCollection(CollectionDTO collectionDTO) {
        Collection collection = collectionMapper.mapToCollection(collectionDTO);
        Collection savedCollection = collectionRepository.save(collection);
        return collectionMapper.mapToCollectionDTO(savedCollection);
    }

    @Override
    @Transactional
    public CollectionDTO updateCollection(long id, String title, String description, LocalDateTime endTime) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new CollectionNotFoundException("Collection with id " + id
                        + " not found in database"));
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
