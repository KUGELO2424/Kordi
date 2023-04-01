package pl.kucharski.Kordi.service.collection.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kucharski.Kordi.enums.CollectionStatus;
import pl.kucharski.Kordi.enums.ItemCategory;
import pl.kucharski.Kordi.enums.ItemType;
import pl.kucharski.Kordi.model.collection.Collection;
import pl.kucharski.Kordi.model.collection_item.CollectionItem;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pl.kucharski.Kordi.CollectionData.createCollectionWithId;

@ExtendWith(MockitoExtension.class)
class CollectionHelperTest {

    private final CollectionHelper underTest = new CollectionHelper();

    private static Collection COLLECTION_WITH_ID;

    @BeforeEach
    void setUp() {
        COLLECTION_WITH_ID = createCollectionWithId();
    }

    @Test
    void shouldChangeStatusToCompletedIfEndTimeBeforeNow() {
        // given
        COLLECTION_WITH_ID.setEndTime(LocalDateTime.now().minusDays(5));

        // when
        Collection updatedCollection = underTest.updateStatus(COLLECTION_WITH_ID);

        // then
        assertEquals(CollectionStatus.COMPLETED, updatedCollection.getStatus());
    }

    @Test
    void shouldChangeStatusToCompletedIfAllItemsCollected() {
        // given
        CollectionItem item = new CollectionItem("item", ItemType.AMOUNT, ItemCategory.OTHER, 10, 10);
        COLLECTION_WITH_ID.addItem(item);

        // when
        Collection updatedCollection = underTest.updateStatus(COLLECTION_WITH_ID);

        // then
        assertEquals(CollectionStatus.COMPLETED, updatedCollection.getStatus());
    }

    @Test
    void shouldChangeStatusToInProgressIfItemsNotCollectedAndEndTimeIsNull() {
        // given
        CollectionItem item = new CollectionItem("item", ItemType.AMOUNT, ItemCategory.OTHER, 0, 10);
        COLLECTION_WITH_ID.addItem(item);
        COLLECTION_WITH_ID.setStatus(CollectionStatus.COMPLETED);

        // when
        Collection updatedCollection = underTest.updateStatus(COLLECTION_WITH_ID);

        // then
        assertEquals(CollectionStatus.IN_PROGRESS, updatedCollection.getStatus());
    }

    @Test
    void shouldChangeStatusToInProgressIfItemsNotCollectedAndEndTimeInFuture() {
        // given
        CollectionItem item = new CollectionItem("item", ItemType.AMOUNT, ItemCategory.OTHER, 0, 10);
        COLLECTION_WITH_ID.addItem(item);
        COLLECTION_WITH_ID.setEndTime(LocalDateTime.now().plusDays(5));
        COLLECTION_WITH_ID.setStatus(CollectionStatus.COMPLETED);

        // when
        Collection updatedCollection = underTest.updateStatus(COLLECTION_WITH_ID);

        // then
        assertEquals(CollectionStatus.IN_PROGRESS, updatedCollection.getStatus());
    }

    @Test
    void shouldChangeStatusToArchivedIfCompletedTimeBeforeOneMoth() {
        // given
        COLLECTION_WITH_ID.setCompletedTime(LocalDateTime.now().minusMonths(1).minusDays(1));
        COLLECTION_WITH_ID.setStatus(CollectionStatus.COMPLETED);

        // when
        Collection updatedCollection = underTest.updateStatus(COLLECTION_WITH_ID);

        // then
        assertEquals(CollectionStatus.ARCHIVED, updatedCollection.getStatus());
    }

}