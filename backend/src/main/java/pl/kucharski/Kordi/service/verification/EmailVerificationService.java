package pl.kucharski.Kordi.service.verification;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kucharski.Kordi.enums.VerificationStatus;
import pl.kucharski.Kordi.model.user.UserDTO;
import pl.kucharski.Kordi.model.email.EmailToken;
import pl.kucharski.Kordi.model.user.User;
import pl.kucharski.Kordi.exception.UserRegisterException;
import pl.kucharski.Kordi.exception.UserVerifyException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.UUID;

import static pl.kucharski.Kordi.config.ErrorCodes.EMAIL_ALREADY_CONFIRMED;
import static pl.kucharski.Kordi.config.ErrorCodes.EMAIL_FAILED;
import static pl.kucharski.Kordi.config.ErrorCodes.EMAIL_FAILED_CONNECTION;
import static pl.kucharski.Kordi.config.ErrorCodes.TOKEN_EXPIRED;
import static pl.kucharski.Kordi.config.ErrorCodes.TOKEN_NOT_FOUND;

/**
 * @author Grzegorz Kucharski gelo2424@wp.pl
 */
@Service
@Transactional
public class EmailVerificationService implements VerificationService{

    @Value("${allowed.origins}")
    private String origin;

    private final JavaMailSender mailSender;
    private final EmailTokenService emailTokenService;


    public EmailVerificationService(JavaMailSender mailSender, EmailTokenService emailTokenService) {
        this.mailSender = mailSender;
        this.emailTokenService = emailTokenService;
    }

    /**
     * @see VerificationService#send(User)
     */
    @Override
    @Async
    public VerificationStatus send(User user) {
        String token = RandomStringUtils.random(6, false, true);
        EmailToken emailToken = new EmailToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                null,
                user
        );
        emailTokenService.saveEmailToken(emailToken);
        String link = origin + "/verify/token/" + token + "/user/" + user.getUsername();
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(buildEmail(user.getFirstName(), link, token, false), true);
            helper.setTo(user.getEmail());
            helper.setSubject("Zweryfikuj swój email");
            helper.setFrom("help@kordi.com");
            mailSender.send(mimeMessage);
        } catch(MailSendException e) {
            throw new UserRegisterException(EMAIL_FAILED_CONNECTION);
        } catch(MessagingException e) {
            throw new UserRegisterException(EMAIL_FAILED);
        }
        return VerificationStatus.PENDING;
    }

    /**
     * @see VerificationService#verify(UserDTO, String)
     */
    @Override
    public VerificationStatus verify(UserDTO user, String token) {
        EmailToken emailToken = emailTokenService.getToken(token)
                .orElseThrow(() -> new IllegalStateException(TOKEN_NOT_FOUND));
        if (emailToken.getConfirmedAt() != null) {
            throw new UserVerifyException(EMAIL_ALREADY_CONFIRMED);
        }
        LocalDateTime expiredAt = emailToken.getExpiresAt();
        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new UserVerifyException(TOKEN_EXPIRED);
        }
        emailTokenService.setConfirmedAt(token);

        return VerificationStatus.VERIFIED;
    }

    private String buildEmail(String name, String link, String token, boolean sendActivationLink) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Potwierdź email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                getMainContent(name, link, token, sendActivationLink) +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }

    private String getMainContent(String name, String link, String token, boolean sendActivationLink) {
        if (sendActivationLink) {
            return "<p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hej " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Dziękujemy za rejestracje w aplikacji Kordi. Kliknij w poniższy link aby aktywować konto: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Aktywuj konto</a> </p></blockquote>\n Link wygaśnie w przeciągu 15 minut. <p>Do zobaczenia!</p>";
        }
        return "<p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hej " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Dziękujemy za rejestracje w aplikacji Kordi. Użyj poniższego kodu by zweryfikować konto: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> " + token + " </p></blockquote>\n Podany kod jest ważny 15 minut. <p>Do zobaczenia!</p>";
    }
}
