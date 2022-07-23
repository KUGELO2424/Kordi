package pl.kucharski.Kordi.model.collection_submitted_item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;

@Getter
@AllArgsConstructor
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
