package tests;

public class RegexpTester {
    public static void main(String[] args) {
        if ("127.0.0.1".matches("(^127\\.(([1-2]([0-5]?[0-5]))|[0-9]{1,2})\\.(([1-2]([0-5]?[0-5]))|[0-9]{1,2})\\.(([1-2]([0-5]?[0-5]))|[0-9]{1,2}))")){
            System.out.println("equals");
        }else System.out.println("not equals");
    }
}
