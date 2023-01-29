package pl.kucharski.Kordi.model.collection_submitted_item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

import static pl.kucharski.Kordi.config.ErrorCodes.AMOUNT_CANNOT_BE_EMPTY;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class SubmittedItemDTO {

    @NotBlank(message = AMOUNT_CANNOT_BE_EMPTY)
    private int amount;
    private LocalDateTime submitTime;
    private String username;
    private String itemName;
    private String itemType;
    private Long userId;
    private Long collectionId;
    @NotBlank
    private Long collectionItemId;

}
