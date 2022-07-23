package pl.kucharski.Kordi.model.address;

public final class AddressMapper {

    public static AddressDTO mapAddressDTOFromAddress(Address address) {
        return AddressDTO.builder()
                .city(address.getCity())
                .street(address.getStreet())
                .build();
    }

    public static Address mapAddressFromAddressDTO(AddressDTO addressDTO) {
        return new Address(addressDTO.getCity(), addressDTO.getStreet());
    }

}
