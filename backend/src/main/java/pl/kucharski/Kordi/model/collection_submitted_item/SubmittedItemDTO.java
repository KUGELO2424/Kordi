package pl.kucharski.Kordi.model.collection_submitted_item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class SubmittedItemDTO {

    private int amount;
    private LocalDateTime submitTime;
    private String username;
    private Long userId;
    private Long collectionId;
    private Long collectionItemId;

}
