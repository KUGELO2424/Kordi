package pl.kucharski.Kordi.service.verification;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kucharski.Kordi.model.email.EmailToken;
import pl.kucharski.Kordi.repository.EmailTokenRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class EmailTokenServiceTest {

    @Mock
    private EmailTokenRepository tokenRepository;
    private EmailTokenService underTest;

    @BeforeEach
    void setUp() {
        underTest = new EmailTokenService(tokenRepository);
    }

    @Test
    void shouldSaveToken() {
        // given
        EmailToken emailToken = new EmailToken("testToken", LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15), null, null);

        // when
        underTest.saveEmailToken(emailToken);

        // then
        ArgumentCaptor<EmailToken> emailTokenArgumentCaptor = ArgumentCaptor.forClass(EmailToken.class);
        verify(tokenRepository).save(emailTokenArgumentCaptor.capture());
    }

    @Test
    void shouldGetToken() {
        // given
        EmailToken emailToken = new EmailToken("testToken", LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15), null, null);
        given(tokenRepository.findByToken("testToken")).willReturn(Optional.of(emailToken));

        // when
        Optional<EmailToken> result = underTest.getToken("testToken");

        // then
        Assertions.assertTrue(result.isPresent());
        assertEquals(result.get(), emailToken);
    }

    @Test
    void shouldUpdateConfirmedAtAttribute() {
        // when
        underTest.setConfirmedAt("testToken");
        LocalDateTime currentTime = LocalDateTime.now();
        // then
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<LocalDateTime> dateArgumentCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(tokenRepository).updateConfirmedAt(stringArgumentCaptor.capture(), dateArgumentCaptor.capture());
    }

}