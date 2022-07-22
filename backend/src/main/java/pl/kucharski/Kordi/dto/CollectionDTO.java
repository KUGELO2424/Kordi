package pl.kucharski.Kordi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Getter
@AllArgsConstructor
@ToString
public class CollectionDTO {

    private Long id;
    private String title;
    private String description;
    private Date startTime;
    private Date endTime;
    private String userFirstname;
    private String userLastname;
    private List<AddressDTO> addresses;
    private List<CollectionItemDTO> items;

}
