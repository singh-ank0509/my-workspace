package com.uppcl.service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.uppcl.data.RequestStatus;
import com.uppcl.model.ReportRequest;
import com.uppcl.repo.ReportRequestRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
	
	private final ReportRequestRepository reportRequestRepository;
	private final RestTemplate restTemplate;
	private final String REPORTS_DIRECTORY = "/Users/ankurkumar/Documents/uppcl";

//	@Override
	public String updateReportRequestNew() throws IOException {
		Optional<ReportRequest> reportRequestOpt = reportRequestRepository.findByRequestStatus();
		String token = null;
		Map<Object, Object> data;
		byte[] excelReport = null;
		
		if(reportRequestOpt.isPresent()) {
			ReportRequest reportRequest = reportRequestOpt.get();
			
			UriComponentsBuilder uriBuilder = UriComponentsBuilder
												.fromHttpUrl(reportRequest.getAuthenticationApi())
												.queryParam("grant_type", "password")
												.queryParam("username", "uppclgenx")
												.queryParam("password", "APimTDu!2019@");
			
			HttpHeaders headers = new HttpHeaders();			
			headers.setBasicAuth("NWxKVGxXZjdWUzlZeGI1ZkgxRUIwYWxGYUVRYTpJdjFvV29GY19IQ1hRWkFHTk4wY00yNXRmOThh");
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Object> response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.POST, entity, Object.class);
			if (response.getStatusCode().is2xxSuccessful()) {
				data = (Map<Object, Object>) response.getBody();
				token = data.get("access_token").toString();
			}
			
			uriBuilder = UriComponentsBuilder.fromHttpUrl(reportRequest.getRequestApi());
			headers.setBearerAuth(token);
			response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, entity, Object.class);
			if (response.getStatusCode().is2xxSuccessful()) {
				data = (Map<Object, Object>) response.getBody();
				List<Map<Object, Object>> result = (List<Map<Object, Object>>) data.get("result");
				if(!result.isEmpty()) {
					excelReport = generateExcel(result);
				}
			}
			
			if (excelReport != null) {
                String filePath = saveExcelToFile(excelReport);
                return "File generated and saved at: " + filePath;
            }
        }
		return "No Pending Requests!!";
	}

	public static byte[] generateExcel(Object response) throws IOException {
        List<Map<Object, Object>> data = (List<Map<Object, Object>>) response;
        if (data == null) {
            throw new IllegalArgumentException("data not found in the response");
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        if (!data.isEmpty()) {
            Row headerRow = sheet.createRow(0);

            Map<Object, Object> firstRow = data.get(0);
            int cellNum = 0;

            for (Object header : firstRow.keySet()) {
                Cell cell = headerRow.createCell(cellNum++);
                cell.setCellValue(header.toString());
            }

            AtomicInteger rowNum = new AtomicInteger(1);

            data.forEach(rowData -> {
                Row row = sheet.createRow(rowNum.getAndIncrement());
                int currentCellNum = 0;

                for (Object header : firstRow.keySet()) {
                    Object value = rowData.get(header);
                    Cell cell = row.createCell(currentCellNum++);
                    if (value != null) {
                        if (value instanceof String) {
                            cell.setCellValue((String) value);
                        } else if (value instanceof Number) {
                            cell.setCellValue(((Number) value).doubleValue());
                        } else if (value instanceof Boolean) {
                            cell.setCellValue((Boolean) value);
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    }
                }
            });
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }
	
	@Override
	public String updateReportRequest() throws IOException {
	    Optional<ReportRequest> reportRequestOpt = reportRequestRepository.findByRequestStatus();
	    String token = null;
	    Map<Object, Object> data;
	    
	    if (reportRequestOpt.isPresent()) {
	    	ReportRequest reportRequest = reportRequestOpt.get();
	        reportRequest.setStartTime(LocalDateTime.now());
	        reportRequest.setRequestStatus(RequestStatus.IN_PROGRESS);
	        reportRequestRepository.save(reportRequest);
	        
	        try {
	        	UriComponentsBuilder uriBuilder = UriComponentsBuilder
						.fromHttpUrl(reportRequest.getAuthenticationApi())
						.queryParam("grant_type", "password")
						.queryParam("username", "uppclgenx")
						.queryParam("password", "APimTDu!2019@");

	        	HttpHeaders headers = new HttpHeaders();
	        	headers.setBasicAuth("NWxKVGxXZjdWUzlZeGI1ZkgxRUIwYWxGYUVRYTpJdjFvV29GY19IQ1hRWkFHTk4wY00yNXRmOThh");

	        	HttpEntity<?> entity = new HttpEntity<>(headers);
	        	ResponseEntity<Object> response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.POST, entity, Object.class);
	        	if (response.getStatusCode().is2xxSuccessful()) {
	        		data = (Map<Object, Object>) response.getBody();
	        		token = data.get("access_token").toString();
	        	}

	        	Workbook workbook = new XSSFWorkbook();
	        	Sheet sheet = workbook.createSheet("Data");
	        	Row headerRow = sheet.createRow(0);

	        	Map<Object, Object> firstRow = new HashMap<>();
	        	String nextPageToken = null;
	        	int rowNum = 1;

	        	do {
	        		uriBuilder = UriComponentsBuilder.fromHttpUrl(reportRequest.getRequestApi());
	        		headers.setBearerAuth(token);
	        		if (nextPageToken != null) {
	        			uriBuilder.queryParam("nextPageToken", nextPageToken);
	        		}

	        		System.err.println(uriBuilder.build(false).toUriString());
	        		response = restTemplate.exchange(uriBuilder.build(false).toUriString(), HttpMethod.GET, entity, Object.class);
	        		if (response.getStatusCode().is2xxSuccessful()) {
	        			data = (Map<Object, Object>) response.getBody();
	        			List<Map<Object, Object>> result = (List<Map<Object, Object>>) data.get("result");
	        			if (!result.isEmpty()) {
	        				if (firstRow.isEmpty()) {
	        					firstRow = result.get(0);
	        					int cellNum = 0;
	        					for (Object header : firstRow.keySet()) {
	        						Cell cell = headerRow.createCell(cellNum++);
	        						cell.setCellValue(header.toString());
	        					}
	        				}

	        				for (Map<Object, Object> rowData : result) {
	        					Row row = sheet.createRow(rowNum++);
	        					int currentCellNum = 0;

	        					for (Object header : firstRow.keySet()) {
	        						Object value = rowData.get(header);
	        						Cell cell = row.createCell(currentCellNum++);
	        						if (value != null) {
	        							if (value instanceof String) {
	        								cell.setCellValue((String) value);
	        							} else if (value instanceof Number) {
	        								cell.setCellValue(((Number) value).doubleValue());
	        							} else if (value instanceof Boolean) {
	        								cell.setCellValue((Boolean) value);
	        							} else {
	        								cell.setCellValue(value.toString());
	        							}
	        						}
	        					}
	        				}
	        			}
	        			nextPageToken = (String) data.get("nextPageToken");
	        		}
	        	} while (nextPageToken != null);

	        	String filePath = saveExcelToFile(workbook);
	        	
	        	reportRequest.setEndTime(LocalDateTime.now());
	        	reportRequest.setRequestStatus(RequestStatus.COMPLETED);
	        	reportRequest.setCsvFilePath(filePath);
	        	reportRequestRepository.save(reportRequest);
	        	
	        	return "File generated and saved at: " + filePath;	        
	        } catch(Exception e) {
	        	e.printStackTrace();
	        	reportRequest.setEndTime(LocalDateTime.now());
	        	reportRequest.setRequestStatus(RequestStatus.FAILED);
	        }
	    }
	    return "No Pending Requests!!";
	}

	private String saveExcelToFile(Workbook workbook) throws IOException {
	    Files.createDirectories(Paths.get(REPORTS_DIRECTORY));

	    String fileName = "report_" + System.currentTimeMillis() + ".xlsx";
	    Path filePath = Paths.get(REPORTS_DIRECTORY, fileName);
	    
	    try (FileOutputStream fileOut = new FileOutputStream(filePath.toFile())) {
	        workbook.write(fileOut);
	    }
	    workbook.close();
	    return filePath.toString();
	}
	
	private String saveExcelToFile(byte[] excelData) throws IOException {
	    String directory = "/Users/ankurkumar/Documents/uppcl";
	    Files.createDirectories(Paths.get(directory));

	    String fileName = "report_" + System.currentTimeMillis() + ".xlsx";
	    System.err.println(fileName);
	    Path filePath = Paths.get(directory, fileName);
	    
	    Files.write(filePath, excelData);
	    return filePath.toString();
	}
}
