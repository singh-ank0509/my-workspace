package java8streams.example.interview;

public class Test2 {

	public String m1() {
		try {
			System.err.println(1/0);
			return "m1() returned... ";
		} catch (Exception e) {
			System.exit(0);
		} finally {
			System.err.println("finally block executed...");
		}
		return "completed";
	}
	
	public static void main(String[] args) {
		Test2 obj = new Test2();
		System.out.println(obj.m1());
	}
}
