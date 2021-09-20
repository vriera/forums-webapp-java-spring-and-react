package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Question;

public interface MailingService {
    public void sendMail(String to, String subject, String body);
    public void sendAnswerVerify(String to, Question question, Answer answer);

}
