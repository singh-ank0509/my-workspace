package java8streams.example.sorting;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ReverseOrderSortingExample {

	public static void main(String[] args) {
		List<String> names = Arrays.asList("Alice", "Eve", "Bob", "Charlie", "David");
		
		// natural sorting order
//		List<String> sortedNames = names.stream()
//										.sorted()
//										.collect(Collectors.toList());
		
		// reversed sorting order - approach 1
//		List<String> sortedNames = names.stream()
//										.sorted(Comparator.reverseOrder())
//										.collect(Collectors.toList());
		
		// reversed sorting order - approach 2
		List<String> sortedNames = names.stream()
//										.sorted((o1, o2) -> o1.compareTo(o2)) // natural sorting order
										.sorted((o1, o2) -> -o1.compareTo(o2)) // reversed sorting order
										.collect(Collectors.toList());
		
		System.err.println(sortedNames);
	}
}
