package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.services.MailingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
public class MailingServiceImpl implements MailingService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    @Async
    public void sendMail(String to, String subject, String body)
    {
        try{
            MimeMessage mimeMsg = javaMailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMsg, false, "utf-8");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            javaMailSender.send(mimeMsg);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
