package java8streams.example.filter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilterStringExammple {

	public static void main(String[] args) {
		List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David", "Eve");
		
		Stream<String> namesStream = names.stream();
		List<String> filteredName = namesStream.filter(name -> name.length() > 3).collect(Collectors.toList());
		
		System.out.println(filteredName);
	}
}
