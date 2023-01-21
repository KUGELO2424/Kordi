package pl.kucharski.Kordi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.kucharski.Kordi.model.collection_submitted_item.SubmittedItem;

public interface SubmittedItemRepository extends JpaRepository<SubmittedItem, Long> {

    Page<SubmittedItem> findByUserUsername(String username, Pageable pageable);
}
