package pl.kucharski.Kordi.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kucharski.Kordi.enums.CollectionStatus;
import pl.kucharski.Kordi.model.collection.Collection;
import pl.kucharski.Kordi.repository.CollectionRepository;
import pl.kucharski.Kordi.service.collection.impl.CollectionHelper;

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
    private final CollectionHelper collectionHelper;


    public CollectionUpdateScheduler(CollectionRepository collectionRepository, CollectionHelper collectionHelper) {
        this.collectionRepository = collectionRepository;
        this.collectionHelper = collectionHelper;
    }

    /***
     * Find collection with endTime in the past and update status.
     */
    @Transactional
    @Scheduled(cron = "${config.collection-update.cron}")
    public void updateCollectionStatusesToCompleted() {
        List<Collection> collections =  collectionRepository.findAllByEndTimeBeforeAndStatus(LocalDateTime.now(), CollectionStatus.IN_PROGRESS);
        collections.forEach(collectionHelper::updateStatus);
    }

    /***
     * Find collection with completedTime one month before and update status.
     */
    @Transactional
    @Scheduled(cron = "${config.collection-update.cron}")
    public void updateCollectionStatusesToArchived() {
        List<Collection> collections =  collectionRepository.findAllByCompletedTimeBeforeAndStatus(LocalDateTime.now().minusMonths(1), CollectionStatus.COMPLETED);
        collections.forEach(collectionHelper::updateStatus);
    }

}
