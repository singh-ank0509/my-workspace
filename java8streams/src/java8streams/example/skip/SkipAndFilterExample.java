package java8streams.example.skip;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SkipAndFilterExample {

	public static void main(String[] args) {
		List<Integer> numbers = Arrays.asList(6, 2, 9, 3, 8, 13, 1);
		
		List<Integer> skippedAndFilteredNumber = numbers.stream()
														.filter(num -> num % 2 == 0)
														.skip(1)
														.collect(Collectors.toList());
		
		System.err.println(skippedAndFilteredNumber);
	}
}
