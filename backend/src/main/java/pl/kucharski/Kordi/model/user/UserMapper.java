package pl.kucharski.Kordi.model.user;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = PasswordEncoderMapper.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class UserMapper {

    public abstract UserDTO mapToUserDTO(User user);

    @Mapping(target = "password", source= "user.password", qualifiedBy = EncodedMapping.class)
    public abstract User mapToUser(UserRegistrationDTO user);

}

