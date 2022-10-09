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

    boolean existsById(Long collectionId);

    List<Collection> findByUserUsername(String username, Pageable pageable);

    List<Collection> findByTitleContaining(String title, Pageable pageable);

    @Query(value =
            "SELECT DISTINCT c FROM Collection c " +
                    "LEFT JOIN c.addresses a " +
                    "WHERE c.title LIKE CONCAT('%',?1,'%') " +
                    "AND a.city LIKE CONCAT('%',?2,'%') " +
                    "AND a.street LIKE CONCAT('%',?3,'%') ")
    List<Collection> findByTitleAndAddress(String title, String city, String street, Pageable pageable);

    @Query(value =
            "SELECT DISTINCT c FROM Collection c " +
                    "LEFT JOIN c.addresses a " +
                    "LEFT JOIN c.items i " +
                    "WHERE c.title LIKE CONCAT('%',?1,'%') " +
                    "AND a.city LIKE CONCAT('%',?2,'%') " +
                    "AND a.street LIKE CONCAT('%',?3,'%') " +
                    "AND i.name LIKE CONCAT('%', ?4, '%')")
    List<Collection> findByTitleAndAddressAndItem(String title, String city, String street, String itemName,
                                                  Pageable pageable);
}
