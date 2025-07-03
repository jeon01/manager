import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.Scanner;
import java.time.LocalDateTime;

public class LoginService {
    private LocalDateTime workStartTime;

    public LocalDateTime getWorkStartTime() {
        return workStartTime;
    }

    public Employee login() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("아이디를 입력하세요: ");
        String id = scanner.nextLine();
        System.out.print("비밀번호를 입력하세요: ");
        String password = scanner.nextLine();

            try (Connection con = DBUtil.getConnection()) {
                String sql = "SELECT NAME FROM EMPLOYEE WHERE EMP_ID = ? AND PASSWORD = ?";
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setString(1, id);
                preparedStatement.setString(2, password);

                ResultSet rs = preparedStatement.executeQuery();

                if (rs.next()) {
                    String name = rs.getString("name");
                    System.out.println(name + " 안녕하세요.");

                    saveloginhistory(id, name);

                    System.out.print("본인이 맞습니까? (Y/N): ");
                    String confirm = scanner.nextLine();

                    if (confirm.equalsIgnoreCase("Y")) {

                        workStartTime = LocalDateTime.now();
                        System.out.println("출근 시간이 기록되었습니다: " + workStartTime);
                        return new Employee(id, name);
                    } else {
                        System.out.println("확인이 취소되어 프로그램을 종료합니다.");
                        return null;
                    }

                } else {
                    System.out.println("로그인 실패: 아이디 또는 비밀번호가 잘못되었습니다.");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        private void saveloginhistory(String empid, String empname){
        String sql = "INSERT INTO LOGIN_HISTORY (EMP_ID, EMP_NAME) VALUES (?, ?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(sql)) {
             preparedStatement.setString(1, empid);
             preparedStatement.setString(2, empname);

            preparedStatement.executeUpdate();

        }catch(Exception e) {
            System.out.println("로그인 기록 저장 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
    }
}