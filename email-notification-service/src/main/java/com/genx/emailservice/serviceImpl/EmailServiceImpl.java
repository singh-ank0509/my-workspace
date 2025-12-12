package com.genx.emailservice.serviceImpl;
 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
 
import com.genx.emailservice.model.EmailRequest;
import com.genx.emailservice.service.EmailService;
 
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
 
 
@Service
public class EmailServiceImpl implements EmailService {
 
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String defaultFrom;
    
    private static final long MAX_FILE_SIZE = 25 * 1024 * 1024;
 
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
 
    @Override
    public void sendEmail(EmailRequest request) {
        try {
           
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper emailBuilder = new MimeMessageHelper(message, true);
 
           
            emailBuilder.setFrom(defaultFrom);
            emailBuilder.setTo(request.getTo().toArray(new String[0]));
            if (request.getCc() != null && !request.getCc().isEmpty()) {
                emailBuilder.setCc(request.getCc().toArray(new String[0]));
            }
 
            emailBuilder.setSubject(request.getSubject());
            emailBuilder.setText(request.getBody(), true);
 
            if (request.getAttachments() != null) {
                for (MultipartFile file : request.getAttachments()) {
                    if (file != null && !file.isEmpty()) {
                        if (file.getSize() > MAX_FILE_SIZE) {
                            throw new RuntimeException("Attachment \"" + file.getOriginalFilename() +
                                    "\" exceeds the 25MB size limit.");
                        }

                        emailBuilder.addAttachment(file.getOriginalFilename(), file);
                    }
                }
            }
 
            mailSender.send(message);
 
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
 