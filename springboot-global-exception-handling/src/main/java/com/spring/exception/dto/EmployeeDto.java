package com.spring.exception.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDto {

	private Long id;
	private String name;
	private Integer age;
	private String email;
	private String department;
	private String phoneNo;
	private LocalDate dateOfJoining;
}
