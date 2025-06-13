package Library;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    public static Connection getConnection() throws Exception {
    	String url = "jdbc:mysql://localhost:3306/oop_library_system";
        String user = "root"; 
        String password = "Min@SQL2025"; 

        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, user, password);
    }
}
