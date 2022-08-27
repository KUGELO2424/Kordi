package pl.kucharski.Kordi.model.user;

public final class UserMapper {

    public static UserDTO mapUserDTOFromUser(User user) {
        return UserDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .enabled(user.isEnabled())
                .build();
    }

}
