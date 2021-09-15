package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;

public interface MailingService {
    public void sendMail(String to, String subject, String body);
    public void sendAnswerVeify(String to, Question question, Answer answer);
    public void verifyEmail(String to, User user);
}
