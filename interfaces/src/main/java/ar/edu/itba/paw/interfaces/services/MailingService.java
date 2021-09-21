package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Question;

public interface MailingService {
    void sendMail(String to, String subject, String body);
    void sendAnswerVerify(String to, Question question, Answer answer);

}
