package pl.kucharski.Kordi.controller;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;
import pl.kucharski.Kordi.KordiApplication;
import pl.kucharski.Kordi.enums.VerificationStatus;
import pl.kucharski.Kordi.model.email.EmailToken;
import pl.kucharski.Kordi.service.verification.EmailTokenService;
import pl.kucharski.Kordi.service.verification.PhoneVerificationService;

import javax.mail.internet.MimeMessage;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.kucharski.Kordi.config.ErrorCodes.EMAIL_ALREADY_EXISTS;
import static pl.kucharski.Kordi.config.ErrorCodes.TOKEN_EXPIRED;
import static pl.kucharski.Kordi.config.ErrorCodes.USER_ALREADY_VERIFIED;
import static pl.kucharski.Kordi.config.ErrorCodes.USER_BAD_CREDENTIALS;
import static pl.kucharski.Kordi.config.ErrorCodes.USER_NOT_FOUND;
import static pl.kucharski.Kordi.config.ErrorCodes.USER_NOT_FOUND_WITH_GIVEN_TOKEN;
import static pl.kucharski.Kordi.config.ErrorCodes.USER_NOT_VERIFIED_EMAIL;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = KordiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserControllerTest {

    private static final int START_OFFSET_RESPONSE_TOKEN = 17;
    private static final int END_OFFSET_RESPONSE_TOKEN = 202;
    private static final String TOKEN_TO_VERIFY = "qwerty123456";
    private static final String LOGIN_USERNAME = "gelo2424";
    private static final String NOT_EXISTING_LOGIN_USERNAME = "not-exising-username";
    private static final String LOGIN_PASSWORD = "qwerty";
    private static final String USER_PHONE = "198321555";
    private static final String NOT_VERIFIED_USERNAME = "test";
    private static final String USER_TO_REGISTER_WITH_TOO_SHORT_PASSWORD =
            "{\"firstName\":\"test\", " +
                    "\"lastName\":\"test\"," +
                    "\"username\":\"test123\"," +
                    "\"password\":\"short\"," +
                    "\"email\":\"test@gmai.pl\"," +
                    "\"verificationType\":\"EMAIL\"," +
                    "\"phone\":\"" + USER_PHONE + "\"}";
    private static final String USER_TO_REGISTER_WITH_PHONE_VERIFICATION =
            "{\"firstName\":\"test\", " +
            "\"lastName\":\"test\"," +
            "\"username\":\"test123\"," +
            "\"password\":\"qwerty\"," +
            "\"email\":\"test@gmai.pl\"," +
            "\"verificationType\":\"PHONE\"," +
            "\"phone\":\"" + USER_PHONE + "\"}";

    private static final String USER_TO_REGISTER_WITH_EMAIL_VERIFICATION =
            "{\"firstName\":\"test2\", " +
                    "\"lastName\":\"test2\"," +
                    "\"username\":\"test1234\"," +
                    "\"password\":\"qwerty\"," +
                    "\"email\":\"test2@gmai.pl\"," +
                    "\"verificationType\":\"EMAIL\"," +
                    "\"phone\":\"876555444\"}";
    private static final String USER_THAT_EXISTS = "{\"firstName\":\"test\", " +
            "\"lastName\":\"test\"," +
            "\"username\":\"newUser\"," +
            "\"password\":\"qwerty\"," +
            "\"email\":\"gelo@gmail.com\"," +
            "\"verificationType\":\"EMAIL\"," +
            "\"phone\":\"" + USER_PHONE + "\"}";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private EmailTokenService emailTokenService;

    @MockBean
    private PhoneVerificationService phoneVerificationService;

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig())
            .withPerMethodLifecycle(false);


    @AfterEach
    void tearDown() {
        greenMail.reset();
    }

    @Test
    public void shouldReturnValidTokenOnValidCredentials() throws Exception {
        MockHttpServletResponse response = mvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("username", LOGIN_USERNAME)
                        .param("password", LOGIN_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token", is(notNullValue())))
                .andReturn().getResponse();
        String token = response.getContentAsString().substring(START_OFFSET_RESPONSE_TOKEN, END_OFFSET_RESPONSE_TOKEN);
        assertNotNull(token);
        String headerWithToken = "Bearer ".concat(token);
        mvc.perform(get("/users")
                        .header("Authorization", headerWithToken))
                .andExpect(status().is(200));
    }

    @Test
    public void shouldReturnUnauthorizedOnInvalidCredentials() throws Exception {
        mvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("username", LOGIN_USERNAME)
                        .param("password", "worngPassword"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is(USER_BAD_CREDENTIALS)));
    }

    @Test
    public void shouldReturnUnauthorizedIfUserNotEnabled() throws Exception {
        mvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("username", NOT_VERIFIED_USERNAME)
                        .param("password", LOGIN_PASSWORD))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is(USER_NOT_VERIFIED_EMAIL)));
    }

    @Test
    @WithMockUser
    public void shouldReturnUsers() throws Exception {

        mvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(7)))
                .andExpect(jsonPath("$[0].username", is("gelo2424")))
                .andExpect(jsonPath("$[1].username", is("adam")))
                .andExpect(jsonPath("$[2].username", is("jan")));
    }

    @Test
    @WithMockUser
    public void shouldReturnUser() throws Exception {

        mvc.perform(get("/users/" + LOGIN_USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(LOGIN_USERNAME)));
    }

    @Test
    @WithMockUser
    public void shouldThrowUserNotFound() throws Exception {
        mvc.perform(get("/users/" + NOT_EXISTING_LOGIN_USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldNotRegisterUserIfPasswordIsTooShort() throws Exception {
        mvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USER_TO_REGISTER_WITH_TOO_SHORT_PASSWORD))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @Test
    public void shouldRegisterUserWithEmailVerification() throws Exception {
        mvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USER_TO_REGISTER_WITH_EMAIL_VERIFICATION))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("PENDING")));

        MimeMessage[] received = greenMail.getReceivedMessages();
        assertEquals(1, received.length);
        MimeMessage receivedMessage = received[0];
        assertEquals("test2@gmai.pl", receivedMessage.getAllRecipients()[0].toString());
    }

    @Test
    public void shouldSendVerificationTokenAgain() throws Exception {
        mvc.perform(post("/sendToken?username=" + NOT_VERIFIED_USERNAME)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("PENDING")));

        MimeMessage[] received = greenMail.getReceivedMessages();
        assertEquals(1, received.length);
        MimeMessage receivedMessage = received[0];
        assertEquals("test@gmail.com", receivedMessage.getAllRecipients()[0].toString());
    }

    @Test
    public void shouldNotSendVerificationTokenAgainIfUserAlreadyVerified() throws Exception {
        mvc.perform(post("/sendToken?verificationType=EMAIL&username=" + LOGIN_USERNAME)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(USER_ALREADY_VERIFIED));
    }

    @Test
    public void shouldNotSendVerificationTokenAgainIfUserNotExists() throws Exception {
        mvc.perform(post("/sendToken?verificationType=EMAIL&username=NotExisting")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldRegisterUserWithPhoneVerification() throws Exception {
        when(phoneVerificationService.send(any())).thenReturn(VerificationStatus.PENDING);
        mvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USER_TO_REGISTER_WITH_PHONE_VERIFICATION))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("PENDING")));
    }

    @Test
    public void shouldThrow400IfEmailAlreadyTaken() throws Exception {
        mvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USER_THAT_EXISTS))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(EMAIL_ALREADY_EXISTS)));
    }

    @Test
    public void shouldThrow400IfTokenAlreadyExpired() throws Exception {
        mvc.perform(get("/verify")
                        .queryParam("token", TOKEN_TO_VERIFY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(TOKEN_EXPIRED)));
    }

    @Test
    public void shouldVerifyUserWithEmailVerification() throws Exception {
        mvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USER_TO_REGISTER_WITH_EMAIL_VERIFICATION))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("PENDING")));

        EmailToken emailToken = emailTokenService.getTokenByUserId(9L);

        mvc.perform(get("/verify")
                        .queryParam("token", emailToken.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("VERIFIED")));
    }

    @Test
    public void shouldVerifyUserWithPhoneVerification() throws Exception {
        when(phoneVerificationService.send(any())).thenReturn(VerificationStatus.PENDING);
        when(phoneVerificationService.verify(any(), any())).thenReturn(VerificationStatus.VERIFIED);
        mvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USER_TO_REGISTER_WITH_PHONE_VERIFICATION))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("PENDING")));

        mvc.perform(get("/verify")
                        .queryParam("token", "someToken")
                        .queryParam("phone", USER_PHONE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("VERIFIED")));
    }

    @Test
    public void shouldThrow404IfNoUserWithGivenPhoneNumber() throws Exception {
        mvc.perform(get("/verify")
                        .queryParam("token", "someToken")
                        .queryParam("phone", "notExistingPhone"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(USER_NOT_FOUND)));
    }

    @Test
    @WithAnonymousUser
    public void shouldThrow400IfNoUserToVerify() throws Exception {
        mvc.perform(get("/verify")
                        .queryParam("token", "NotExistingToken"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(USER_NOT_FOUND_WITH_GIVEN_TOKEN)));
    }

    @Test
    public void shouldThrow400IfOldPasswordDoesNotMatch() throws Exception {
        String token = logInUser();
        String headerWithToken = "Bearer ".concat(token);
        mvc.perform(put("/users/updatePassword")
                        .header("Authorization", headerWithToken)
                        .queryParam("oldPassword", "notExisting")
                        .queryParam("password", "newPassword"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldThrow400IfNewPasswordIsTooShort() throws Exception {
        String token = logInUser();
        String headerWithToken = "Bearer ".concat(token);
        mvc.perform(put("/users/updatePassword")
                        .header("Authorization", headerWithToken)
                        .queryParam("oldPassword", LOGIN_PASSWORD)
                        .queryParam("password", "short"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void shouldUpdateUserPassword() throws Exception {
        String token = logInUser();
        String headerWithToken = "Bearer ".concat(token);
        mvc.perform(put("/users/updatePassword")
                        .header("Authorization", headerWithToken)
                        .queryParam("oldPassword", LOGIN_PASSWORD)
                        .queryParam("password", "newPassword"))
                .andExpect(status().isOk());

        mvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("username", LOGIN_USERNAME)
                        .param("password", "newPassword"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token", is(notNullValue())));
    }

    private String logInUser() throws Exception {
        MockHttpServletResponse response = mvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("username", LOGIN_USERNAME)
                        .param("password", LOGIN_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token", is(notNullValue())))
                .andReturn().getResponse();
        return response.getContentAsString().substring(START_OFFSET_RESPONSE_TOKEN, END_OFFSET_RESPONSE_TOKEN);

    }

}