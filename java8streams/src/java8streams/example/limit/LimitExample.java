package java8streams.example.limit;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LimitExample {

	public static void main(String[] args) {
		List<Integer> numbers = Arrays.asList(60, 20, 90, 30, 80, 130, 10);
		
		// Get first 3 elements of list
		List<Integer> firstThreeNumbers = numbers.stream()
												.limit(3)
												.collect(Collectors.toList());
		
		System.err.println(firstThreeNumbers);
	}
}
