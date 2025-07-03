import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Scanner;

public class PosApp {

    private static int balance = 1234000;

    public static void main(String[] args) {
        LoginService loginService = new LoginService();
        Employee employee = loginService.login();

        if(employee!=null){
            System.out.println("출근 시간: " + loginService.getWorkStartTime());
            System.out.println("다음 단계로 진행합니다.");

            System.out.println("현재 잔고: " + balance + "원");

            Scanner scanner = new Scanner(System.in);

            System.out.print("결제할 금액 입력: ");
            int payAmount = Integer.parseInt(scanner.nextLine());

            if(pay(payAmount)){
                System.out.println("결제 완료, 남은 잔고: " + balance + "원");
            }else {
                System.out.println("잔고가 부족하여 결제가 실패됐습니다.");
            }

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

    public static boolean pay(int amount){
        if(balance>=amount){
            balance-=amount;
            return true;
        }else{
            return false;
        }
    }
}
