package pl.kucharski.Kordi.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import pl.kucharski.Kordi.CollectionMapperTestImpl;
import pl.kucharski.Kordi.enums.ItemType;
import pl.kucharski.Kordi.exception.CollectionNotFoundException;
import pl.kucharski.Kordi.model.address.AddressDTO;
import pl.kucharski.Kordi.model.collection.Collection;
import pl.kucharski.Kordi.model.collection.CollectionDTO;
import pl.kucharski.Kordi.model.collection_item.CollectionItemDTO;
import pl.kucharski.Kordi.model.collection_item.CollectionItemMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@DataJpaTest
@Transactional
class CollectionRepositoryTest {

    private static final Pageable paging = PageRequest.of(0, 10);
    private static final Pageable paging_with_title_sort = PageRequest.of(0, 10, Sort.by("title"));
    private static final Pageable paging_with_start_time_sort = PageRequest.of(0, 10, Sort.by("startTime"));
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final LocalDateTime start_time_01 = LocalDateTime.parse("2022-06-28 15:15", formatter);
    private static final LocalDateTime start_time_02 = LocalDateTime.parse("2022-06-28 15:25", formatter);
    private static final LocalDateTime start_time_03 = LocalDateTime.parse("2022-06-28 15:45", formatter);
    private static final LocalDateTime start_time_04 = LocalDateTime.parse("2022-06-28 15:55", formatter);

    private static final AddressDTO ADDRESS_DTO_TO_SAVE = AddressDTO.builder()
            .city("Warszawa")
            .street("Wielka")
            .build();

    private static final CollectionDTO COLLECTION_DTO_TO_SAVE = CollectionDTO.builder()
            .title("")
            .description("description")
            .startTime(CURRENT_TIME)
            .userId(1L)
            .addresses(List.of(ADDRESS_DTO_TO_SAVE))
            .build();

    private static final CollectionItemDTO COLLECTION_ITEM_DTO = CollectionItemDTO.builder()
            .name("item")
            .currentAmount(0)
            .maxAmount(10)
            .type(ItemType.AMOUNT)
            .build();

    @Autowired
    private CollectionRepository underTest;

    private final CollectionItemMapper itemMapper = Mappers.getMapper(CollectionItemMapper.class);
    private final CollectionMapperTestImpl collectionMapper = new CollectionMapperTestImpl();


    @Test
    void shouldFindCollectionsByUsername() {
        // given
        String username = "ewa";

        // when
        List<Collection> collections = underTest.findByUserUsername(username, paging);

        // then
        Assertions.assertEquals(2, collections.size());
        Assertions.assertEquals("Zbiórka dla Bartka", collections.get(0).getTitle());
        Assertions.assertEquals("Zbiórka dla Oliwii", collections.get(1).getTitle());
    }

    @Test
    void shouldFindZeroCollectionsByUsername() {
        // given
        String username = "gelo2424";

        // when
        List<Collection> collections = underTest.findByUserUsername(username, paging);

        // then
        Assertions.assertEquals(0, collections.size());
    }

    @Test
    void shouldFindCollectionsByTitle() {
        // given
        String title = "Oliwii";

        // when
        List<Collection> collections = underTest.findByTitleContaining(title, paging);

        // then
        Assertions.assertEquals(2, collections.size());
    }

    @Test
    void shouldFindCollectionsByTitleAndAddress() {
        // given
        String title = "Oliwii";
        String city = "Łódź";
        String street = "Piotrkowska";

        // when
        List<Collection> collections = underTest.findByTitleAndAddress(title, city, street, paging);

        // then
        Assertions.assertEquals(1, collections.size());
        Assertions.assertEquals("Zbiórka dla Oliwii", collections.get(0).getTitle());
    }

    @Test
    void shouldFindCollectionsByAddress() {
        // given
        String title = "";
        String city = "Warszawa";
        String street = "";

        // when
        List<Collection> collections = underTest.findByTitleAndAddress(title, city, street, paging);

        // then
        Assertions.assertEquals(1, collections.size());
    }

    @Test
    void shouldFindCollectionsByTitleAndAddressAndItemName() {
        // given
        String title = "Zbiórka";
        String city = "Łódź";
        String street = "";
        String itemName = "Buty";

        // when
        List<Collection> collections = underTest.findByTitleAndAddressAndItem(title, city, street, itemName, paging);

        // then
        Assertions.assertEquals(1, collections.size());
        Assertions.assertEquals("Zbiórka dla Bartka", collections.get(0).getTitle());
    }

    @Test
    void shouldFindCollectionsByItemName() {
        // given
        String title = "";
        String city = "";
        String street = "";
        String itemName = "Buty";

        // when
        List<Collection> collections = underTest.findByTitleAndAddressAndItem(title, city, street, itemName, paging);

        // then
        Assertions.assertEquals(1, collections.size());
        Assertions.assertEquals("Zbiórka dla Bartka", collections.get(0).getTitle());
    }

    @Test
    void shouldFindCollectionsAndSortThemByTitle() {
        // given
        String title = "";

        // when
        List<Collection> collections = underTest.findByTitleContaining(title, paging_with_title_sort);

        // then
        Assertions.assertEquals(4, collections.size());
        Assertions.assertEquals("Dary dary dla Oliwii", collections.get(0).getTitle());
        Assertions.assertEquals("Pomoc dla Plamy", collections.get(1).getTitle());
        Assertions.assertEquals("Zbiórka dla Bartka", collections.get(2).getTitle());
        Assertions.assertEquals("Zbiórka dla Oliwii", collections.get(3).getTitle());
    }

    @Test
    void shouldFindCollectionsAndSortThemByCreatedDate() {
        // given
        String title = "";

        // when
        List<Collection> collections = underTest.findByTitleContaining(title, paging_with_start_time_sort);

        // then
        Assertions.assertEquals(4, collections.size());
        Assertions.assertEquals(start_time_01, collections.get(0).getStartTime());
        Assertions.assertEquals(start_time_02, collections.get(1).getStartTime());
        Assertions.assertEquals(start_time_03, collections.get(2).getStartTime());
        Assertions.assertEquals(start_time_04, collections.get(3).getStartTime());
    }

    @Test
    void shouldSaveCollection() {
        // when
        Collection savedCollection = underTest.save(collectionMapper.mapToCollection(COLLECTION_DTO_TO_SAVE));

        // then
        Assertions.assertEquals(5, savedCollection.getId());
        Assertions.assertEquals(5, savedCollection.getAddresses().get(0).getId());
    }

    @Test
    void shouldSaveCollectionItem() {
        // when
        Collection collection = underTest.findById(1L).orElseThrow(CollectionNotFoundException::new);
        collection.addItem(itemMapper.mapToCollectionItem(COLLECTION_ITEM_DTO));
        Collection savedCollection = underTest.save(collection);

        // then
        Assertions.assertEquals(1, savedCollection.getId());
        Assertions.assertEquals(4, savedCollection.getItems().size());
        Assertions.assertEquals("item", savedCollection.getItems().get(3).getName());
    }

}