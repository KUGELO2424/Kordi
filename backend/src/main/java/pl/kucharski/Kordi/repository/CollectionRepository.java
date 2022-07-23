package pl.kucharski.Kordi.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.kucharski.Kordi.model.collection.Collection;

import java.util.List;

/**
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {
    List<Collection> findByUserUsername(String username, Pageable pageable);

    List<Collection> findByTitleContaining(String title, Pageable pageable);

    @Query(nativeQuery = true, value =
            "SELECT * FROM collection LEFT JOIN address ON collection.id = address.collection_id " +
                    "WHERE collection.title LIKE CONCAT('%',?1,'%') " +
                    "AND address.city LIKE CONCAT('%',?2,'%') " +
                    "AND address.street LIKE CONCAT('%',?3,'%') ")
    List<Collection> findByTitleAndAddress(String title, String city, String street, Pageable pageable);

    @Query(nativeQuery = true, value =
            "SELECT * FROM collection LEFT JOIN address ON collection.id = address.collection_id " +
                    "LEFT JOIN collection_item ON collection.id = collection_item.collection_id " +
                    "WHERE collection.title LIKE CONCAT('%',?1,'%') " +
                    "AND address.city LIKE CONCAT('%',?2,'%') " +
                    "AND address.street LIKE CONCAT('%',?3,'%') " +
                    "AND collection_item.name LIKE CONCAT('%', ?4, '%')")
    List<Collection> findByTitleAndAddressAndItem(String title, String city, String street, String itemName,
                                                  Pageable pageable);
}
