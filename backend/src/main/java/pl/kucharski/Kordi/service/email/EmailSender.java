package pl.kucharski.Kordi.service.email;

public interface EmailSender {
    void send(String to, String email);
}
