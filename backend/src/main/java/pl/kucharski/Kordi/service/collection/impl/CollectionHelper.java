package pl.kucharski.Kordi.service.collection.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.kucharski.Kordi.enums.CollectionStatus;
import pl.kucharski.Kordi.model.collection.Collection;
import pl.kucharski.Kordi.model.collection_item.CollectionItem;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class CollectionHelper {

    Collection updateStatus(Collection collection) {
        LocalDateTime endTime = collection.getEndTime();
        if (collection.getStatus().equals(CollectionStatus.IN_PROGRESS)) {
            if (endTime != null && endTime.isBefore(LocalDateTime.now()) || areItemsCollected(collection)) {
                collection.setStatus(CollectionStatus.COMPLETED);
                collection.setCompletedTime(LocalDateTime.now());
                log.info("Collection " + collection.getId() + " status changed to COMPLETED");
            }
        } else if (collection.getStatus().equals(CollectionStatus.COMPLETED)) {
            if ((!areItemsCollected(collection) && endTime != null && endTime.isAfter(LocalDateTime.now())) ||
                    (!areItemsCollected(collection) && endTime == null)) {
                collection.setStatus(CollectionStatus.IN_PROGRESS);
                collection.setCompletedTime(null);
                log.info("Collection " + collection.getId() + " status changed to IN_PROGRESS");
            }
        }
        return collection;
    }

    private boolean areItemsCollected(Collection collection) {
        Optional<CollectionItem> notCollectedItem = collection.getItems().stream()
                .filter(item -> item.getCurrentAmount() != item.getMaxAmount()).findAny();
        return notCollectedItem.isEmpty();
    }

}
