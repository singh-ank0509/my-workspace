package java8streams.example.filter;

import java.util.*;
import java.util.stream.Collectors;

public class FilterEmployeeExample {

	public static void main(String[] args) {
		
		List<Employee> employees = Arrays.asList(new Employee("Alice", 25),
				 								new Employee("Bob", 28),
				 								new Employee("Charlie", 35),
				 								new Employee("David", 23));

		List<Employee> filteredEmployee = employees.stream()
												.filter(employee -> employee.getAge() > 25)
												.collect(Collectors.toList());
		
		System.err.println(filteredEmployee);
	}
}
