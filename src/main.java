import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        LoginService loginService = new LoginService();
        Employee employee = loginService.login();

        if (employee == null) {
            System.out.println("로그인 실패 또는 취소, 프로그램을 종료합니다.");
            return;
        }

        LocalDateTime workStartTime = loginService.getWorkStartTime();
        System.out.println("출근 시간: " + workStartTime);
        System.out.println("다음 단계로 넘어갑니다.");

        List<Product> products = PosApp.loadProductsFromDB();
        PosApp.setProducts(products);

        Scanner scanner = new Scanner(System.in);

        final int hourlyWage = 10000;

        while (true) {
            System.out.println("\n<<제품 관리 메뉴>>");
            System.out.println("1. 제품 입력");
            System.out.println("2. 제품 목록 보기");
            System.out.println("3. 결제하기");
            System.out.println("4. 재고 확인");
            System.out.println("5. 물품 입고");
            System.out.println("6. 19금 제품 확인");
            System.out.println("7. 원하는 날짜 매출 조회");
            System.out.println("8. 제품 찾기");
            System.out.println("9. 종료");
            System.out.print("선택-> ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    PosApp.addProduct(scanner);
                    break;
                case "2":
                    PosApp.showProducts();
                    break;
                case "3":
                    PosApp.purchaseProducts(scanner);
                    break;
                case "4":
                    PosApp.showStockWithStars();
                    break;
                case "5":
                    products = PosApp.loadProductsFromDB();
                    PosApp.setProducts(products);
                    PosApp.restockProducts();
                    break;
                case "6":
                    PosApp.showRestrictedProducts();
                    break;
                case "7":
                    PosApp.viewSalesByDate(scanner);
                    break;
                case "8":
                    PosApp.searchProducts(scanner);
                    break;
                case "9":
                    if (PosApp.canExit()) {
                        LocalDateTime workEndTime = LocalDateTime.now();
                        Duration duration = Duration.between(workStartTime, workEndTime);
                        long minutesWorked = duration.toMinutes();

                        double dailyPay = (hourlyWage / 60.0) * minutesWorked;

                        System.out.println("프로그램을 종료합니다.");
                        System.out.println("근무 시간: " + minutesWorked + "분");
                        System.out.printf("오늘 일당: %.0f원\n", dailyPay);
                        System.out.println("사원 : " + employee.getName() + " 빠이");

                        return;
                    } else {
                        System.out.println("제품을 최소 10개 이상 입력해야 종료할 수 있습니다.");
                    }
                    break;
                default:
                    System.out.println("잘못된 입력입니다. 다시 선택하세요.");
                    break;
            }
        }
    }
}