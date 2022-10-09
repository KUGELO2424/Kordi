package pl.kucharski.Kordi.model.collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class CollectionUpdateDTO {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime endTime;

}
