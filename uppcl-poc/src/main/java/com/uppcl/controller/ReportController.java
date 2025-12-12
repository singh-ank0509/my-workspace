package com.uppcl.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;

import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uppcl.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReportController {
	
	private final ReportService reportService;
	private final String REPORTS_DIRECTORY = "/Users/ankurkumar/Documents/uppcl";

	@PostMapping("/upload")
	public String updateReportRequest() throws IOException {		
		return reportService.updateReportRequest();
	}
	
	@GetMapping("/download")
    public void downloadFile(@RequestParam String fileName,
    						HttpServletResponse response) throws IOException {
        try {
            Path filePath = Paths.get(REPORTS_DIRECTORY).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                throw new IOException("File not found: " + fileName);
            }

            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            response.setContentLength((int) Files.size(filePath));

            Files.copy(filePath, response.getOutputStream());
            response.getOutputStream().flush();
        } catch (MalformedURLException e) {
            throw new IOException("Error while loading file: " + fileName, e);
        }
    }
}
