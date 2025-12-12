package com.genx.emailservice.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

@Data
public class EmailRequest {
    private List<String> to;
    private List<String> cc;
    private String subject;
    private String body;
    private List<MultipartFile> attachments;
}
