package java8streams.example.map;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import java8streams.example.filter.Employee;

public class MapEmployeeExample {

	public static void main(String[] args) {
		List<Employee> employees = Arrays.asList(new Employee("Alice", 25),
												 new Employee("Bob", 28),
												 new Employee("Charlie", 35),
												 new Employee("David", 23));
		
		List<String> names = employees.stream()
									.map(emp -> emp.getName())
									.collect(Collectors.toList());
		
		System.err.println(names);
	}
}
