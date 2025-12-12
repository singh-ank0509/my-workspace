package java8streams.example.map;

import java.util.*;
import java.util.stream.Collectors;

public class MapIntegerExample {

	public static void main(String[] args) {
		List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
		
		List<Integer> square = numbers.stream()
									.map(num -> num*num)
									.collect(Collectors.toList());
		
		System.out.println(square);
	}
}
