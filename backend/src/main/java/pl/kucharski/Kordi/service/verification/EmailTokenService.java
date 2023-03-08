package pl.kucharski.Kordi.service.verification;

import org.springframework.stereotype.Service;
import pl.kucharski.Kordi.model.email.EmailToken;
import pl.kucharski.Kordi.repository.EmailTokenRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;

/**
 * @author Grzegorz Kucharski gelo2424@wp.pl
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
     * @param userId id of user
     */
    public EmailToken getTokenByUserId(Long userId) {
        return tokenRepository.findByUserId(userId).stream()
                .max(Comparator.comparing(EmailToken::getCreatedAt))
                .orElseThrow(IllegalAccessError::new);
    }

    /**
     * Set new confirmed at date to token entity
     *
     * @param token which we want to update confirmed at date
     */
    public void setConfirmedAt(String token) {
        tokenRepository.updateConfirmedAt(token, LocalDateTime.now());
    }
}
