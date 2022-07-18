package pl.kucharski.Kordi.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pl.kucharski.Kordi.entity.Collection;

import java.util.List;

@DataJpaTest
class CollectionRepositoryTest {

    private static final Pageable paging = PageRequest.of(0, 10);

    @Autowired
    private CollectionRepository underTest;

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
    void shouldFindCollectionsByTitleAndAddressAndItemName() {
        // given
        String title = "Zbiórka";
        String city = "Łódź";
        String street = "";
        String itemName = "Buty";

        // when
        List<Collection> collections = underTest.findByTitleAndAddressAndItems(title, city, street, itemName, paging);

        // then
        Assertions.assertEquals(1, collections.size());
        Assertions.assertEquals("Zbiórka dla Bartka", collections.get(0).getTitle());
    }

}