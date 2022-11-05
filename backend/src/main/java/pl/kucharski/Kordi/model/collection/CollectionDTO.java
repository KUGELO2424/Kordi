package pl.kucharski.Kordi.model.collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.kucharski.Kordi.model.address.AddressDTO;
import pl.kucharski.Kordi.model.collection_item.CollectionItemDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
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
    private Long userId;
    private String userFirstname;
    private String userLastname;
    @Builder.Default
    private List<AddressDTO> addresses = new ArrayList<>();
    @Builder.Default
    @Valid
    private List<CollectionItemDTO> items = new ArrayList<>();


}
