package com.spring.exception;

import java.time.LocalDate;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.spring.exception.entity.EmployeeEntity;
import com.spring.exception.repository.EmployeeRepo;

@SpringBootApplication
public class SpringbootGlobalExceptionHandlingApplication implements CommandLineRunner {

	@Value("${CERT_VERIFICATION_URL}")
	private String url;
	
	@Autowired
	private EmployeeRepo empRepo;
	
	public static void main(String[] args) {
		SpringApplication.run(SpringbootGlobalExceptionHandlingApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		System.err.println(url);
		EmployeeEntity emp1 = EmployeeEntity.builder()
											.name("user1")
											.age(20)
											.department("dp1")
											.email("user1@gmail.com")
											.phoneNo("9876543210")
											.dateOfJoining(LocalDate.of(2021, 3, 21))
											.build();
		
		EmployeeEntity emp2 = EmployeeEntity.builder()
											.name("user2")
											.age(20)
											.department("dp1")
											.email("user2@gmail.com")
											.phoneNo("9876543211")
											.dateOfJoining(LocalDate.of(2021, 7, 23))
											.build();
		
		EmployeeEntity emp3 = EmployeeEntity.builder()
											.name("user3")
											.age(23)
											.department("dp2")
											.email("user3@gmail.com")
											.phoneNo("9876543212")
											.dateOfJoining(LocalDate.of(2023, 9, 22))
											.build();
		
		EmployeeEntity emp4 = EmployeeEntity.builder()
											.name("user4")
											.age(21)
											.department("dp5")
											.email("user4@gmail.com")
											.phoneNo("9876543213")
											.dateOfJoining(LocalDate.of(2021, 1, 12))
											.build();
		empRepo.saveAll(Arrays.asList(emp1, emp2, emp3, emp4));	
	}	
}
