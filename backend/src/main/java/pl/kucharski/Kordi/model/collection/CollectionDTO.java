package pl.kucharski.Kordi.model.collection;

import lombok.*;
import pl.kucharski.Kordi.model.address.AddressDTO;
import pl.kucharski.Kordi.model.collection_item.CollectionItemDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class CollectionDTO {

    private Long id;
    @NotBlank(message = "Title cannot be empty")
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @NotNull
    private Long userId;
    private String userFirstname;
    private String userLastname;
    @Builder.Default
    private List<AddressDTO> addresses = new ArrayList<>();
    @Builder.Default
    private List<CollectionItemDTO> items = new ArrayList<>();


}
