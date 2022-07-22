package pl.kucharski.Kordi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class AddressDTO {

    private String city;
    private String street;

}
