import java.sql.*;

public class EmployeeDAO {

    public Employee getEmployeeByIdAndPassword(String empId, String password) {
        String sql = "SELECT name FROM employee WHERE emp_id = ? AND password = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, empId);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    return new Employee(empId, name);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}