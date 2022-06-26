package pl.kucharski.Kordi.service.verification;

import org.springframework.stereotype.Service;
import pl.kucharski.Kordi.entity.EmailToken;
import pl.kucharski.Kordi.repository.EmailTokenRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EmailTokenService {

    EmailTokenRepository tokenRepository;

    public EmailTokenService(EmailTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public void saveEmailToken(EmailToken token) {
        tokenRepository.save(token);
    }

    public Optional<EmailToken> getToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public int setConfirmedAt(String token) {
        return tokenRepository.updateConfirmedAt(token, LocalDateTime.now());
    }
}
