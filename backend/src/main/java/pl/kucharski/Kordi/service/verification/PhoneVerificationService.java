package pl.kucharski.Kordi.service.verification;

import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.kucharski.Kordi.entity.User;

@Service
public class PhoneVerificationService implements VerificationService{

    @Value("${TWILIO_ACCOUNT_SID}")
    private String ACCOUNT_SID;

    @Value("${TWILIO_AUTH_TOKEN}")
    private String AUTH_TOKEN;

    @Value("${TWILIO_SERVICE_ID}")
    private String SERVICE_ID;

    @Override
    public String send(User user) {
        Verification verification = null;
        String phoneNumber = "+48" + user.getPhone();;

        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        verification = Verification.creator(
                        SERVICE_ID,
                        phoneNumber,
                        "sms")
                .create();

        return verification.getStatus();
    }

    @Override
    public String verify(User user, String token) {
        VerificationCheck verificationCheck = null;
        String phoneNumber = "+48" + user.getPhone();

        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        verificationCheck = VerificationCheck.creator(
                        SERVICE_ID,
                        token)
                .setTo(phoneNumber).create();

        if (verificationCheck.getValid()) {
            return verificationCheck.getStatus();
        } else {
            throw new IllegalStateException("Cannot verify your account");
        }
    }
}