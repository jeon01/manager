import java.sql.*;

public class LoginHistoryDAO {

    public void insertLoginHistory(String empId, String empName) {
        String sql = "INSERT INTO login_history (emp_id, emp_name) VALUES (?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, empId);
            pstmt.setString(2, empName);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("로그인 기록 저장 중 오류 발생:");
            e.printStackTrace();
        }
    }
}