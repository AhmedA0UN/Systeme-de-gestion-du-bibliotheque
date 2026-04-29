package util;

import java.sql.Connection;
public class Test {
	public static void main(String[]args) {
		Connection cn=SingletonConnection.getInstance();
	}
}
