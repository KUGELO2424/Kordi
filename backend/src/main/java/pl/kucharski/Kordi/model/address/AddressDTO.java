package pl.kucharski.Kordi.model.address;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
@Builder
public class AddressDTO {

    private String city;
    private String street;

}
