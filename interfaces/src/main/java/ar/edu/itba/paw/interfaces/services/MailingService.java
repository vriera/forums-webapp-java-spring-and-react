package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;

import java.util.Locale;

public interface MailingService {
    void sendMail(String to, String subject, String body);

    void verifyEmail(String to, User user, String baseUrl, Locale locale);

    void sendAnswerVerify(String to, Question question, Answer answer, String baseUrl, Locale locale);
}
