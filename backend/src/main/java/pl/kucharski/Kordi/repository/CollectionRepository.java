package pl.kucharski.Kordi.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.kucharski.Kordi.entity.Collection;

import java.util.List;

/**
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {
    List<Collection> findByUserUsername(String username, Pageable pageable);
    List<Collection> findByTitleContaining(String title);
    @Query(nativeQuery = true, value = "" +
            "SELECT * FROM collection JOIN address ON collection.id = address.id" +
            "WHERE collection.title LIKE '%:title%' " +
            "AND address.city LIKE '%:city' " +
            "AND address.street '%:street%'")
    List<Collection> findByTitleAndAddress(String title, String city, String street);
}
