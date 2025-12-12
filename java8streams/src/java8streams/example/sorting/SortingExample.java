package java8streams.example.sorting;

import java.util.*;
import java.util.stream.Collectors;

public class SortingExample {

	public static void main(String[] args) {
		List<Integer> numbers = Arrays.asList(6, 2, 9, 3, 8, 13, 1);
		
		List<Integer> ascendingNumbers = numbers.stream()
												.sorted()
												.collect(Collectors.toList());
		
		System.err.println(ascendingNumbers);
	}
}
