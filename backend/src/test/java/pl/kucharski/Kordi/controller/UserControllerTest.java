package pl.kucharski.Kordi.controller;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.kucharski.Kordi.KordiApplication;
import pl.kucharski.Kordi.enums.VerificationStatus;
import pl.kucharski.Kordi.model.email.EmailToken;
import pl.kucharski.Kordi.service.verification.EmailTokenService;
import pl.kucharski.Kordi.service.verification.PhoneVerificationService;

import javax.mail.internet.MimeMessage;
import java.util.Objects;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = KordiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserControllerTest {

    private static final int START_OFFSET_RESPONSE_TOKEN = 17;
    private static final int END_OFFSET_RESPONSE_TOKEN = 202;
    private static final String TOKEN_TO_VERIFY = "qwerty123456";
    private static final String LOGIN_USERNAME = "gelo2424";
    private static final String LOGIN_PASSWORD = "qwerty";
    private static final String USER_PHONE = "198321555";
    private static final String USER_TO_REGISTER_WITH_PHONE_VERIFICATION =
            "{\"firstName\":\"test\", " +
            "\"lastName\":\"test\"," +
            "\"username\":\"test123\"," +
            "\"password\":\"qwerty\"," +
            "\"email\":\"test@gmai.pl\"," +
            "\"phone\":\"" + USER_PHONE + "\"}";

    private static final String USER_TO_REGISTER_WITH_EMAIL_VERIFICATION =
            "{\"firstName\":\"test2\", " +
                    "\"lastName\":\"test2\"," +
                    "\"username\":\"test1234\"," +
                    "\"password\":\"qwerty\"," +
                    "\"email\":\"test2@gmai.pl\"," +
                    "\"phone\":\"876555444\"}";
    private static final String USER_THAT_EXISTS = "{\"firstName\":\"test\", " +
            "\"lastName\":\"test\"," +
            "\"username\":\"newUser\"," +
            "\"password\":\"qwerty\"," +
            "\"email\":\"gelo@gmail.com\"," +
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
    public void shouldReturnUnauthorizedOnUsersGetWhenNoAuthHeader()
            throws Exception {

        mvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isUnauthorized())
                .andExpect(status().reason(containsString("Full authentication is required to access this resource")));
    }

    @Test
    public void shouldReturnUnauthorizedOnInvalidCredentials()
            throws Exception {
        mvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("username", LOGIN_USERNAME)
                        .param("password", "worngPassword"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("Bad credentials")));
    }

    @Test
    @WithMockUser
    public void shouldReturnUsers()
            throws Exception {

        mvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(7)))
                .andExpect(jsonPath("$[0].username", is("gelo2424")))
                .andExpect(jsonPath("$[1].username", is("adam")))
                .andExpect(jsonPath("$[2].username", is("jan")));
    }

    @Test
    public void shouldRegisterUserWithEmailVerification() throws Exception {
        mvc.perform(post("/register?verificationType=EMAIL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USER_TO_REGISTER_WITH_EMAIL_VERIFICATION)
                        .param("username", LOGIN_USERNAME)
                        .param("password", "worngPassword"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("PENDING")));

        MimeMessage[] received = greenMail.getReceivedMessages();
        assertEquals(1, received.length);
        MimeMessage receivedMessage = received[0];
        assertEquals("test2@gmai.pl", receivedMessage.getAllRecipients()[0].toString());
    }

    @Test
    public void shouldRegisterUserWithPhoneVerification() throws Exception {
        when(phoneVerificationService.send(any())).thenReturn(VerificationStatus.PENDING);
        mvc.perform(post("/register?verificationType=PHONE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USER_TO_REGISTER_WITH_PHONE_VERIFICATION)
                        .param("username", LOGIN_USERNAME)
                        .param("password", "worngPassword"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("PENDING")));
    }

    @Test
    public void shouldThrow400IfEmailAlreadyTaken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(USER_THAT_EXISTS, headers);
        ResponseEntity<?> response = testRestTemplate.postForEntity("/register?verificationType=EMAIL", request, String.class);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Email is already in use!", Objects.requireNonNull(response.getBody()).toString());
    }

    @Test
    public void shouldThrow400IfTokenAlreadyExpired() throws Exception {
        mvc.perform(get("/verify")
                        .queryParam("token", TOKEN_TO_VERIFY))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Token expired"));
    }

    @Test
    public void shouldVerifyUserWithEmailVerification() throws Exception {
        mvc.perform(post("/register?verificationType=EMAIL")
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
        mvc.perform(post("/register?verificationType=PHONE")
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
    public void shouldThrow400IfNoUserWithGivenPhoneNumber() throws Exception {
        mvc.perform(get("/verify")
                        .queryParam("token", "someToken")
                        .queryParam("phone", "notExistingPhone"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found in database"));
    }

    @Test
    public void shouldThrow400IfNoUserToVerify() throws Exception {

        mvc.perform(get("/verify")
                        .queryParam("token", "NotExistingToken"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found with given token"));
    }

}