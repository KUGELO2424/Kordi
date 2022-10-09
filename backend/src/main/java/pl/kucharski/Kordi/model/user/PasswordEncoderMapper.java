package pl.kucharski.Kordi.model.user;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderMapper {

    final PasswordEncoder passwordEncoder;

    public PasswordEncoderMapper(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @EncodedMapping
    public String encode(String password) {
        return passwordEncoder.encode(password);
    }

}
