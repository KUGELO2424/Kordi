package pl.kucharski.Kordi.service.verification;

import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.kucharski.Kordi.enums.VerificationStatus;
import pl.kucharski.Kordi.model.user.UserDTO;
import pl.kucharski.Kordi.model.user.User;
import pl.kucharski.Kordi.exception.UserVerifyException;

import static pl.kucharski.Kordi.config.ErrorCodes.VERIFICATION_ERROR;

/**
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */
@Service
public class PhoneVerificationService implements VerificationService{

    @Value("${TWILIO_ACCOUNT_SID}")
    private String ACCOUNT_SID;

    @Value("${TWILIO_AUTH_TOKEN}")
    private String AUTH_TOKEN;

    @Value("${TWILIO_SERVICE_ID}")
    private String SERVICE_ID;

    /**
     * @see VerificationService#send(User)
     */
    @Override
    public VerificationStatus send(User user) {
        Verification verification;
        String phoneNumber = "+48" + user.getPhone();

        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        verification = Verification.creator(
                        SERVICE_ID,
                        phoneNumber,
                        "sms")
                .create();

        if (verification.getStatus().equals("pending")) {
            return VerificationStatus.PENDING;
        }
        return VerificationStatus.REJECTED;
    }

    /**
     * @see VerificationService#verify(UserDTO, String)
     */
    @Override
    public VerificationStatus verify(UserDTO user, String token) {
        VerificationCheck verificationCheck;
        String phoneNumber = "+48" + user.getPhone();

        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        verificationCheck = VerificationCheck.creator(
                        SERVICE_ID,
                        token)
                .setTo(phoneNumber).create();

        if (verificationCheck.getValid()) {
            return VerificationStatus.VERIFIED;
        } else {
            throw new UserVerifyException(VERIFICATION_ERROR);
        }
    }
}
