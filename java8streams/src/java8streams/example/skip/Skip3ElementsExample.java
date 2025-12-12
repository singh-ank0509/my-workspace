package java8streams.example.skip;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Skip3ElementsExample {

	public static void main(String[] args) {
		List<Integer> numbers = Arrays.asList(60, 20, 90, 30, 80, 130, 10);
		
		// skipped first 3 elements
		List<Integer> skippedFirst3Elements = numbers.stream()
													.skip(3)
													.collect(Collectors.toList());
		
		System.err.println(skippedFirst3Elements);
	}
}
