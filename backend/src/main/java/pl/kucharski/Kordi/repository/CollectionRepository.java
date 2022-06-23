package pl.kucharski.Kordi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.kucharski.Kordi.entity.Collection;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {
}
