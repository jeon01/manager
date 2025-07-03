import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Scanner;

public class PosApp {
    public static void main(String[] args) {
        LoginService loginService = new LoginService();
        Employee employee = loginService.login();

        if(employee!=null){
            System.out.println("출근 시간: " + loginService.getWorkStartTime());
            System.out.println("다음 단계로 진행합니다.");

            Scanner scanner = new Scanner(System.in);
            System.out.println("\n종료하려면 아무 키나 누르세요!");
            scanner.nextLine();

            LocalDateTime endtime = LocalDateTime.now();
            LocalDateTime starttime = loginService.getWorkStartTime();

            Duration duration = Duration.between(starttime, endtime);
            long minutesworked = duration.toMinutes();

            int wageperminute = 11000;
            long dailypay = wageperminute * minutesworked;

            System.out.println("\n근무 시간: " + minutesworked + "분");
            System.out.println("오늘의 일당: " + dailypay + "원");
        }else{
            System.out.println("프로그램을 종료합니다.");
        }
    }
}
