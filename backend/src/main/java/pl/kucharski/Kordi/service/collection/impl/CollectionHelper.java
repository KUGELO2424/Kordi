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

    /***
     * This method update collection status.<br>
     * If IN_PROGRESS and endTime before now or items collected -> COMPLETED<br>
     * If COMPLETED and (items not collected and endTime is null) or (items collected and endTime in future) -> IN_PROGRESS<br>
     * If COMPLETED and completedTime one month before now -> ARCHIVED<br>
     */
    public Collection updateStatus(Collection collection) {
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
            } else if (collection.getCompletedTime() != null && collection.getCompletedTime().isBefore(LocalDateTime.now().minusMonths(1))) {
                collection.setStatus(CollectionStatus.ARCHIVED);
                log.info("Collection " + collection.getId() + " status changed to ARCHIVED");
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
