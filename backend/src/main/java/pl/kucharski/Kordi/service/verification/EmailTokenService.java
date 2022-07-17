package pl.kucharski.Kordi.service.verification;

import org.springframework.stereotype.Service;
import pl.kucharski.Kordi.entity.EmailToken;
import pl.kucharski.Kordi.repository.EmailTokenRepository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */
@Service
public class EmailTokenService {

    EmailTokenRepository tokenRepository;

    public EmailTokenService(EmailTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    /**
     * @param token to save
     */
    public void saveEmailToken(EmailToken token) {
        tokenRepository.save(token);
    }

    /**
     * @param token to find
     */
    public Optional<EmailToken> getToken(String token) {
        return tokenRepository.findByToken(token);
    }

    /**
     * Set new confirmed at date to token entity
     *
     * @param token which we want to update confirmed at date
     */
    public int setConfirmedAt(String token) {
        return tokenRepository.updateConfirmedAt(token, LocalDateTime.now());
    }
}
