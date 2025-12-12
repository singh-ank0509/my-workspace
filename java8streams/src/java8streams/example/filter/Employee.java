package java8streams.example.filter;

public class Employee {

	private String name;
	private int age;
	
	public Employee(String name, int age) {
		this.name = name;
		this.age = age;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getAge() {
		return this.age;
	}

	@Override
	public String toString() {
		return name + " " + age;
	}
}
