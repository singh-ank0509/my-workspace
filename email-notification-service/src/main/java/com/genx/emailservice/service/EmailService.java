package com.genx.emailservice.service;

import com.genx.emailservice.model.EmailRequest;

public interface EmailService {
    void sendEmail(EmailRequest request);
}