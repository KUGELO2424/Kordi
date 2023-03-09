package pl.kucharski.Kordi.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.kucharski.Kordi.enums.CollectionStatus;
import pl.kucharski.Kordi.model.collection.Collection;
import pl.kucharski.Kordi.repository.CollectionRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduler responsible for updating collection status
 * @author Grzegorz Kucharski gelo2424@wp.pl
 */
@Slf4j
@Service
public class CollectionUpdateScheduler {

    private final CollectionRepository collectionRepository;


    public CollectionUpdateScheduler(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    /***
     * Update collections status to COMPLETED if endTime in the past.
     */
    @Scheduled(cron = "${config.collection-update.cron}")
    public void updateCollectionStatuses() {
        List<Collection> collections =  collectionRepository.findAllByEndTimeBeforeAndStatus(LocalDateTime.now(), CollectionStatus.IN_PROGRESS);
        collections.forEach(collection -> {
            log.info("Updating collection " + collection.getId() + " status to COMPLETED");
            collectionRepository.updateCollectionStatus(collection.getId(), CollectionStatus.COMPLETED);
        });
    }

}
