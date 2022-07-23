package pl.kucharski.Kordi.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class CollectionDTO {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String userFirstname;
    private String userLastname;
    private List<AddressDTO> addresses;
    private List<CollectionItemDTO> items;

}
