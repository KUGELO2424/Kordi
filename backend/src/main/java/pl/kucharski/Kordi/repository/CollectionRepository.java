package pl.kucharski.Kordi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.kucharski.Kordi.enums.ItemCategory;
import pl.kucharski.Kordi.model.collection.Collection;

import java.util.List;

/**
 * @author Grzegorz Kucharski gelo2424@wp.pl
 */

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {

    boolean existsById(Long collectionId);

    List<Collection> findByUserUsername(String username, Pageable pageable);

    @Query(value =
            "SELECT DISTINCT c FROM Collection c " +
                    "LEFT JOIN c.addresses a " +
                    "LEFT JOIN c.items i " +
                    "WHERE (c.title LIKE CONCAT('%',?1,'%') OR ?1 LIKE '') " +
                    "AND (a.city LIKE CONCAT('%',?2,'%') OR ?2 LIKE '') " +
                    "AND (a.street LIKE CONCAT('%',?3,'%') OR ?3 LIKE '') " +
                    "AND (i.name LIKE CONCAT('%', ?4, '%') OR ?4 LIKE '')" +
                    "AND (?5 < 1 OR ((i.category IN ?6)))")
    Page<Collection> findWithFiltering(String title, String city, String street, String itemName,
                                       int listSize, List<ItemCategory> categories, Pageable pageable);
}
