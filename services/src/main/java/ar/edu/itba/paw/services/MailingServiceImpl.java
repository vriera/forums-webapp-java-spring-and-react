package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.services.MailingService;
import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;

@Service
public class MailingServiceImpl implements MailingService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;


    @Override
    @Async
    public void sendMail(String to, String subject, String body)
    {
        try{
            MimeMessage mimeMsg = javaMailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMsg, false, "utf-8");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body,true);
            javaMailSender.send(mimeMsg);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    @Async
    public void sendAnswerVerify(String to, Question question, Answer answer){
        final String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        final Context context = new Context();
        context.setVariable("answer", answer);
        context.setVariable("question", question);
        context.setVariable("link",baseUrl + "/question/answer/" + answer.getId() + "/verify/");
        String body = this.templateEngine.process("verify", context);
        sendMail(to,"Verificar Respuesta",body);
    }

}
