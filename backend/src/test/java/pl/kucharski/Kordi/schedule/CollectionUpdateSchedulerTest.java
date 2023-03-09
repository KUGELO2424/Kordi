package pl.kucharski.Kordi.schedule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kucharski.Kordi.enums.CollectionStatus;
import pl.kucharski.Kordi.model.collection.Collection;
import pl.kucharski.Kordi.repository.CollectionRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pl.kucharski.Kordi.CollectionData.createCollectionWithId;

@ExtendWith(MockitoExtension.class)
class CollectionUpdateSchedulerTest {

    @Mock
    private CollectionRepository collectionRepository;

    @InjectMocks
    private CollectionUpdateScheduler underTest;

    @Test
    void shouldUpdateCollections() {
        // given
        Collection collection = createCollectionWithId();
        collection.setEndTime(LocalDateTime.now().minusDays(5));
        collection.setStatus(CollectionStatus.IN_PROGRESS);
        when(collectionRepository.findAllByEndTimeBeforeAndStatus(any(), eq(CollectionStatus.IN_PROGRESS)))
                .thenReturn(List.of(collection));

        // when
        underTest.updateCollectionStatuses();

        // then
        verify(collectionRepository).updateCollectionStatus(collection.getId(), CollectionStatus.COMPLETED);
    }

    @Test
    void shouldNotUpdateAnyCollections() {
        // given
        when(collectionRepository.findAllByEndTimeBeforeAndStatus(any(), eq(CollectionStatus.IN_PROGRESS)))
                .thenReturn(Collections.emptyList());

        // when
        underTest.updateCollectionStatuses();

        // then
        verify(collectionRepository, never()).updateCollectionStatus(any(), any());
    }

}