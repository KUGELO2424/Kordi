package pl.kucharski.Kordi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;

@Getter
@AllArgsConstructor
@ToString
public class CollectionSubmittedItemDTO {

    private int amount;
    private Date submitTime;
    private String username;
    private Long collectionId;
    private Long collectionItemId;

}
