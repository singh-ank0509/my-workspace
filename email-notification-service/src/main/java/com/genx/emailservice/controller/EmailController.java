package com.genx.emailservice.controller;
 
import java.util.List;
 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
 
import com.genx.emailservice.Utils.EmailValidator;
import com.genx.emailservice.model.EmailRequest;
import com.genx.emailservice.service.EmailService;
 
import lombok.RequiredArgsConstructor;
 
@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor 
public class EmailController {
 
    private final EmailService emailService;
 
    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(
            @RequestParam("to") List<String> to,
            @RequestParam(value = "cc", required = false) List<String> cc,
            @RequestParam("subject") String subject,
            @RequestParam("body") String body,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {

    	EmailRequest request = new EmailRequest();
        request.setTo(to);
        request.setCc(cc);
        request.setSubject(subject);
        request.setBody(body);
        request.setAttachments(attachments);
        EmailValidator.validateToEmail(request);
        emailService.sendEmail(request);
 
        return ResponseEntity.ok("Email sent successfully");
 
    }

 
 
}