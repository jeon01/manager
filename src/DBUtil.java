import java.sql.Connection;
import java.sql.DriverManager;

// DB 연결
public class DBUtil {
    public static Connection getConnection() throws Exception {
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String user = "c##manager";
        String password = "manager123";
        Class.forName("oracle.jdbc.driver.OracleDriver");
        return DriverManager.getConnection(url, user, password);
    }
}
