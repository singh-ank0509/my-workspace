package java8streams.example.flatMap;

import java.util.*;
import java.util.stream.Collectors;

public class FlatMapExample {

	public static void main(String[] args) {
		List<List<String>>  fruitsList = Arrays.asList(Arrays.asList("Apple", "Banana", "Cherry"),
													   Arrays.asList("Papaya", "Grapes", "Mango"),
													   Arrays.asList("Guava", "Orange", "Carrot"));
		
		List<String> fruits = fruitsList.stream()
										.flatMap(List::stream)
										.collect(Collectors.toList());
		System.err.println(fruits);
	}
}
