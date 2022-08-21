package pl.kucharski.Kordi.model.collection_submitted_item;

import lombok.*;

import java.util.Date;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class SubmittedItemDTO {

    private int amount;
    private Date submitTime;
    private String username;
    private Long userId;
    private Long collectionId;
    private Long collectionItemId;

}
