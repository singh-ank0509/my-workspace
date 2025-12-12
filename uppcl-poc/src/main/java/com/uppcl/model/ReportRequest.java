package com.uppcl.model;

import java.time.LocalDateTime;

import javax.persistence.*;

import com.uppcl.data.RequestStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "report_request")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportRequest {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;

    @Column(name = "request_api", columnDefinition = "TEXT", nullable = false)
    private String requestApi;

    @Column(name = "api_param", columnDefinition = "TEXT")
    private String apiParam;

    @Column(name = "authentication_api", columnDefinition = "TEXT")
    private String authenticationApi;

    @Column(name = "authentication_param", columnDefinition = "TEXT")
    private String authenticationParam;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status", nullable = false)
    private RequestStatus requestStatus;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "csv_file_path", length = 500)
    private String csvFilePath;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
