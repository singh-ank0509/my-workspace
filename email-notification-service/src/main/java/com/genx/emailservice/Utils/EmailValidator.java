package com.genx.emailservice.Utils;

import java.util.List;

import com.genx.emailservice.model.EmailRequest;

public class EmailValidator {
	
	private static final String ALLOWED_DOMAIN = "@gen-xt.com";

    private EmailValidator() {}

//    public static void validateFromEmail(EmailRequest request) {
//        String fromEmail = request.getFrom();
//        if (fromEmail == null || !fromEmail.toLowerCase().endsWith(ALLOWED_DOMAIN)) {
//            throw new IllegalArgumentException("Sender email must be from " + ALLOWED_DOMAIN + " domain.");
//        }
//    }
    
    public static void validateToEmail(EmailRequest request) {
        List<String> toEmails = request.getTo();

        for (String toEmail : toEmails) {
            if (toEmail == null || !toEmail.toLowerCase().endsWith(ALLOWED_DOMAIN)) {
                throw new IllegalArgumentException("Recipient email must be from " + ALLOWED_DOMAIN + " domain.");
            }
        }
    }
}