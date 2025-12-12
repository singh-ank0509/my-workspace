package com.spring.exception.controller;

import java.util.NoSuchElementException;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.spring.exception.dto.EmployeeDto;
import com.spring.exception.entity.EmployeeEntity;
import com.spring.exception.handler.ResourceNotFoundException;
import com.spring.exception.repository.EmployeeRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@ControllerAdvice
@Slf4j
public class EmployeeController {

	private final EmployeeRepo empRepo;
	private final ModelMapper modelMapper;
	
	@GetMapping("/welcome")
	public void welcome() throws InterruptedException {
		Thread.sleep(20000);
		log.info("Welcome call...");
	}
	
	@GetMapping("/greet")
	public void greet() throws InterruptedException {
		log.info("Greet call...");
	}
	
	@GetMapping("/id/{id}")
	public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable String id) {
//		EmployeeEntity emp = empRepo.findById(Long.valueOf(id)).orElseThrow(() -> new ResourceNotFoundException("Employee Not Found!!"));
//		EmployeeDto map = modelMapper.map(emp, EmployeeDto.class);

		try {
			EmployeeEntity emp = empRepo.findById(Long.valueOf(id)).get();
			EmployeeDto map = modelMapper.map(emp, EmployeeDto.class);
			return ResponseEntity.ok(map);
		} catch(Exception e) {
			throw new NoSuchElementException("Employee Not Found!!");
		}	
	}
	
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<String> handleNoSuchElementsException(NoSuchElementException exception) {
		
		return new ResponseEntity<String>("Employee Not Found!!", HttpStatus.NOT_FOUND);
	}
	
	@PostMapping("/emp")
	public ResponseEntity<EmployeeDto> saveEmployee(@RequestBody EmployeeDto dto) {
		EmployeeEntity empEntity = modelMapper.map(dto, EmployeeEntity.class);
		EmployeeEntity savedEmployee = empRepo.save(empEntity);
				
		return ResponseEntity.ok(modelMapper.map(savedEmployee, EmployeeDto.class));
	}
	
	@PutMapping("/update/emp/{id}")
	public ResponseEntity<EmployeeDto> updateEmployeee(@PathVariable String id,
													  @RequestBody EmployeeDto dto) {
		EmployeeEntity empEntity = modelMapper.map(dto, EmployeeEntity.class);
		EmployeeEntity savedEmployee = empRepo.save(empEntity);
				
		return ResponseEntity.ok(modelMapper.map(savedEmployee, EmployeeDto.class));
	}
	
	@PatchMapping("/update/emp/name/{id}")
	public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable String id,
													  @RequestBody EmployeeDto dto) {
		EmployeeEntity emp = empRepo.findById(Long.valueOf(id)).orElseThrow(() -> new ResourceNotFoundException("Employee Not Found!!"));
		emp.setName(dto.getName());
		
		EmployeeEntity savedEmployee = empRepo.save(emp);
				
		return ResponseEntity.ok(modelMapper.map(savedEmployee, EmployeeDto.class));
	}
	
	@PatchMapping("/update/emp/email/{id}")
	public ResponseEntity<EmployeeDto> updateEmployeeEmail(@PathVariable String id,
													  @RequestBody EmployeeDto dto) {
		EmployeeEntity emp = empRepo.findById(Long.valueOf(id)).orElseThrow(() -> new ResourceNotFoundException("Employee Not Found!!"));
		emp.setEmail(dto.getEmail());
		
		EmployeeEntity savedEmployee = empRepo.save(emp);
				
		return ResponseEntity.ok(modelMapper.map(savedEmployee, EmployeeDto.class));
	}
}
