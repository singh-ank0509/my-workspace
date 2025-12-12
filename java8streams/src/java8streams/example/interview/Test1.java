package java8streams.example.interview;

public class Test1 implements interfaceA, interfaceB {

	@Override
	public String m1() {
		return interfaceA.super.m1();
	}
	
	public static void main(String[] args) {
		Test1 obj1  = new Test1();
		System.err.println(obj1.m1());
	}
}
