package pl.kucharski.Kordi.model.address;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressDTO mapToAddressDTO(Address address);
    Address mapToAddress(AddressDTO address);

}
