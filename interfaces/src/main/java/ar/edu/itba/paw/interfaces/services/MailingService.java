package ar.edu.itba.paw.interfaces.services;

public interface MailingService {
    public void sendMail(String to, String subject, String body);
}
