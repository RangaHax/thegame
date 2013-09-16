import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;


public class Test extends Thread {
	public Test() {
		start();
	}
	public static void main(String[] args) {new Test();}
}
class Test2 extends Thread {
	public Test2() {
		start();
	}
}