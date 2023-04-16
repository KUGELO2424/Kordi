package pl.kucharski.Kordi.model.address;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class AddressDTO {

    private Long id;
    private String city;
    private String street;

}
