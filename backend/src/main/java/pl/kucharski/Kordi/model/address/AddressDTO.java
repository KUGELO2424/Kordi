package pl.kucharski.Kordi.model.address;

import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class AddressDTO {

    private String city;
    private String street;

}
