import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.sql.*;

public class PosApp {

    private static int balance = 1234000;
    private static List<Product> products = new ArrayList<>();
    private static Random random = new Random();

    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String DB_USER = "c##manager";
    private static final String DB_PASSWORD = "manager123";

    public static void setProducts(List<Product> productList) {
        products = productList;
    }

    public static List<Product> loadProductsFromDB() {
        List<Product> productsFromDB = new ArrayList<>();
        String sql = "SELECT name, manufacturer, expiration_date, is_restricted, price, stock FROM product_table";

        try (
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery();
        ) {
            while (rs.next()) {
                String name = rs.getString("name");
                String manufacturer = rs.getString("manufacturer");
                LocalDate expirationDate = rs.getDate("expiration_date").toLocalDate();
                boolean isRestricted = rs.getString("is_restricted").equalsIgnoreCase("Y");
                int price = rs.getInt("price");
                int stock = rs.getInt("stock");

                Product product = new Product(name, manufacturer, expirationDate, isRestricted, price, stock);
                productsFromDB.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productsFromDB;
    }

    public static void addProduct(Scanner scanner) {
        System.out.println("=== 새 제품 입력 ===");

        System.out.print("제품명 입력: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("제품명은 필수 입력 항목입니다.");
            return;
        }

        System.out.print("제조회사 입력: ");
        String manufacturer = scanner.nextLine().trim();
        if (manufacturer.isEmpty()) {
            System.out.println("제조회사명은 필수 입력 항목입니다.");
            return;
        }

        System.out.print("유통기한 입력 (yyyy-MM-dd): ");
        String dateStr = scanner.nextLine().trim();
        LocalDate expirationDate;
        try {
            expirationDate = LocalDate.parse(dateStr);
        } catch (Exception e) {
            System.out.println("유통기한 입력 형식이 잘못되었습니다.");
            return;
        }

        System.out.print("19금 물품입니까? (Y/N): ");
        String restrictedInput = scanner.nextLine().trim();
        boolean isRestricted = restrictedInput.equalsIgnoreCase("Y");

        System.out.print("가격 입력 (숫자만): ");
        String priceStr = scanner.nextLine().trim();
        int price;
        try {
            price = Integer.parseInt(priceStr);
            if (price <= 0) {
                System.out.println("가격은 0보다 커야 합니다.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("가격은 숫자만 입력 가능합니다.");
            return;
        }

        System.out.print("재고 수량 입력: ");
        String stockStr = scanner.nextLine().trim();
        int stock;
        try {
            stock = Integer.parseInt(stockStr);
            if (stock < 0) {
                System.out.println("재고 수는 0 이상이어야 합니다.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("재고 수량은 숫자만 입력 가능합니다.");
            return;
        }

        Product product = new Product(name, manufacturer, expirationDate, isRestricted, price, stock);
        products.add(product);
        System.out.println("제품이 성공적으로 등록되었습니다:");
        System.out.println(product);
    }

    public static void showProducts() {
        System.out.println("\n등록된 제품 목록:");
        if (products.isEmpty()) {
            System.out.println("제품이 없습니다.");
        } else {
            for (Product p : products) {
                System.out.println(p);
            }
        }
    }

    public static void purchaseProducts(Scanner scanner) {
        if (products.isEmpty()) {
            System.out.println("제품이 없습니다. 먼저 제품을 입력하세요.");
            return;
        }

        List<Product> selectedProducts = new ArrayList<>();
        System.out.println("\n구매할 제품을 선택하세요. 번호 입력 (종료는 0)");

        while (true) {
            for (int i = 0; i < products.size(); i++) {
                System.out.println((i + 1) + ". " + products.get(i));
            }
            System.out.print("선택 번호: ");
            String input = scanner.nextLine();
            if (input.equals("0")) break;

            int index;
            try {
                index = Integer.parseInt(input) - 1;
                if (index < 0 || index >= products.size()) {
                    System.out.println("잘못된 번호입니다.");
                    continue;
                }
                Product chosen = products.get(index);
                if (chosen.getStock() <= 0) {
                    System.out.println(chosen.getProductName() + "의 재고가 부족합니다.");
                    continue;
                }
                selectedProducts.add(chosen);
                chosen.reduceStock(1);
                System.out.println(chosen.getProductName() + " 선택됨. 남은 재고: " + chosen.getStock());
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력하세요.");
            }
        }

        if (selectedProducts.isEmpty()) {
            System.out.println("구매할 제품이 선택되지 않았습니다.");
            return;
        }

        for (Product p : selectedProducts) {
            if (p.getExpirationDate().isBefore(LocalDate.now())) {
                System.out.println("유통기한 지난 상품이 포함되어 있습니다. 구매할 수 없습니다.");
                for (Product p2 : selectedProducts) {
                    p2.addStock(1);
                }
                return;
            }
        }

        int ageCheckResult = checkAgeForRestricted(scanner, selectedProducts);
        if (ageCheckResult == -1) {
            for (Product p : selectedProducts) {
                p.addStock(1);
            }
            return;
        } else if (ageCheckResult == 0) {
            System.out.println("19세 미만은 19금 제품을 구매할 수 없습니다. 결제 취소.");
            for (Product p : selectedProducts) {
                p.addStock(1);
            }
            return;
        }

        int totalPrice = selectedProducts.stream().mapToInt(Product::getPrice).sum();
        System.out.println("총 결제 금액: " + totalPrice + "원");

        while (true) {
            System.out.println("결제 수단을 선택하세요.");
            System.out.println("1. 카드결제");
            System.out.println("2. 현금결제");
            System.out.print("선택 -> ");
            String paymentMethod = scanner.nextLine();

            if (paymentMethod.equals("1")) {
                if (pay(totalPrice)) {
                    System.out.println("카드결제 완료, 남은 잔고: " + balance + "원");
                    recordSales(selectedProducts);
                    break;
                } else {
                    System.out.println("잔고 부족으로 카드결제 실패");
                }
            } else if (paymentMethod.equals("2")) {
                System.out.print("현금 금액 입력: ");
                String cashStr = scanner.nextLine();
                int cash;
                try {
                    cash = Integer.parseInt(cashStr);
                    if (cash < totalPrice) {
                        System.out.println("현금이 부족합니다.");
                        continue;
                    }
                    int change = cash - totalPrice;
                    System.out.println("거스름돈: " + change + "원");
                    balance += totalPrice;
                    System.out.println("잔고가 " + totalPrice + "원 증가했습니다. 현재 잔고: " + balance + "원");
                    recordSales(selectedProducts);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("숫자를 입력하세요.");
                }
            } else {
                System.out.println("잘못된 선택입니다. 다시 선택하세요.");
            }
        }
    }

    private static int checkAgeForRestricted(Scanner scanner, List<Product> selectedProducts) {
        boolean hasRestricted = false;
        for (Product p : selectedProducts) {
            if (p.isRestricted()) {
                hasRestricted = true;
                break;
            }
        }
        if (!hasRestricted) return 1;

        System.out.print("19금 제품 포함! 주민등록번호 13자리 입력 (ex. 9901011234567): ");
        String jumin = scanner.nextLine().trim();
        if (jumin.length() != 13) {
            System.out.println("잘못된 주민등록번호 길이입니다.");
            return -1;
        }

        char genderCode = jumin.charAt(6);
        int birthYearPrefix;
        if (genderCode == '1' || genderCode == '3' || genderCode == '5' || genderCode == '7') {
            birthYearPrefix = 1900;
        } else if (genderCode == '2' || genderCode == '4' || genderCode == '6' || genderCode == '8') {
            birthYearPrefix = 2000;
        } else if (genderCode == '9' || genderCode == '0') {
            birthYearPrefix = 1800;
        } else {
            System.out.println("잘못된 주민등록번호입니다.");
            return -1;
        }

        int birthYear = birthYearPrefix + Integer.parseInt(jumin.substring(0, 2));
        int birthMonth = Integer.parseInt(jumin.substring(2, 4));
        int birthDay = Integer.parseInt(jumin.substring(4, 6));

        LocalDate birthDate;
        try {
            birthDate = LocalDate.of(birthYear, birthMonth, birthDay);
        } catch (Exception e) {
            System.out.println("잘못된 생년월일 정보입니다.");
            return -1;
        }

        LocalDate today = LocalDate.now();
        int age = today.getYear() - birthDate.getYear();
        if (today.getDayOfYear() < birthDate.getDayOfYear()) {
            age--;
        }

        if (age < 19) {
            return 0;
        }
        return 1;
    }

    public static boolean pay(int amount) {
        if (balance >= amount) {
            balance -= amount;
            return true;
        } else {
            return false;
        }
    }

    public static int getBalance() {
        return balance;
    }

    public static int getProductCount() {
        return products.size();
    }

    public static boolean canExit() {
        return getProductCount() >= 10;
    }

    public static void showStockWithStars() {
        System.out.println("\n[재고 확인]");
        if (products.isEmpty()) {
            System.out.println("등록된 제품이 없습니다.");
            return;
        }

        for (Product p : products) {
            System.out.print(p.getProductName() + " : ");
            for (int i = 0; i < p.getStock(); i++) {
                System.out.print("*");
            }
            System.out.println(" (" + p.getStock() + "개)");
        }
    }

    public static void restockProducts() {
        System.out.println("\n[물품 랜덤 입고 시작]");
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            conn.setAutoCommit(false);

            String selectSql = "SELECT stock FROM product_table WHERE name = ?";
            String updateSql = "UPDATE product_table SET stock = ? WHERE name = ?";

            try (PreparedStatement selectPstmt = conn.prepareStatement(selectSql);
                 PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {

                for (Product p : products) {
                    int incoming = random.nextInt(20) + 1;

                    selectPstmt.setString(1, p.getProductName());
                    ResultSet rs = selectPstmt.executeQuery();

                    int currentStockInDB = 0;
                    if (rs.next()) {
                        currentStockInDB = rs.getInt("stock");
                    }
                    rs.close();

                    int newStock = currentStockInDB + incoming;
                    p.setStock(newStock);

                    updatePstmt.setInt(1, newStock);
                    updatePstmt.setString(2, p.getProductName());
                    updatePstmt.executeUpdate();

                    System.out.println(p.getProductName() + " 입고량: " + incoming + ", 현재 재고: " + newStock);
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("[물품 입고 완료]");
    }

    public static void showRestrictedProducts() {
        if (products.isEmpty()) {
            System.out.println("등록된 제품이 없습니다.");
            return;
        }
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n[19금 제품 확인 메뉴]");
            for (int i = 0; i < products.size(); i++) {
                System.out.println((i + 1) + ". " + products.get(i).getProductName());
            }
            System.out.println("0. 종료");
            System.out.print("확인할 제품 번호 입력: ");
            String input = scanner.nextLine();

            if (input.equals("0")) {
                System.out.println("19금 확인 메뉴를 종료합니다.");
                break;
            }

            int index;
            try {
                index = Integer.parseInt(input) - 1;
                if (index < 0 || index >= products.size()) {
                    System.out.println("잘못된 번호입니다. 다시 입력하세요.");
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력하세요.");
                continue;
            }

            Product selected = products.get(index);
            if (selected.isRestricted()) {
                System.out.println(selected.getProductName() + "는 19금 제품입니다.");
            } else {
                System.out.println(selected.getProductName() + "는 19금 제품이 아닙니다.");
            }
        }
    }

    public static void searchProducts(Scanner scanner) {
        System.out.print("검색할 키워드 입력 (제품명 또는 제조회사): ");
        String keyword = scanner.nextLine().trim().toLowerCase();

        List<Product> matched = new ArrayList<>();
        for (Product p : products) {
            if (p.getProductName().toLowerCase().contains(keyword) ||
                    p.getManufacturer().toLowerCase().contains(keyword)) {
                matched.add(p);
            }
        }

        if (matched.isEmpty()) {
            System.out.println("검색 결과가 없습니다.");
        } else {
            System.out.println("\n검색 결과:");
            for (Product p : matched) {
                System.out.println(p);
            }
        }
    }

    public static void recordSales(List<Product> soldProducts) {
        String sql = "INSERT INTO sales_table (product_name, quantity, total_price, sale_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Product product : soldProducts) {
                pstmt.setString(1, product.getProductName());
                pstmt.setInt(2, 1);
                pstmt.setInt(3, product.getPrice());
                pstmt.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            System.out.println("매출 기록 중 오류 발생:");
            e.printStackTrace();
        }
    }

    public static void viewSalesByDate(Scanner scanner) {
        System.out.print("조회할 날짜 입력 (yyyy-MM-dd): ");
        String dateInput = scanner.nextLine();
        try {
            LocalDate.parse(dateInput);

            String sql = "SELECT product_name, quantity, total_price FROM sales_table WHERE TRUNC(sale_date) = TO_DATE(?, 'YYYY-MM-DD')";
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, dateInput);
                ResultSet rs = pstmt.executeQuery();

                int total = 0;
                boolean found = false;
                System.out.println("\n[" + dateInput + " 매출 내역]");
                while (rs.next()) {
                    found = true;
                    String name = rs.getString("product_name");
                    int qty = rs.getInt("quantity");
                    int price = rs.getInt("total_price");
                    total += price;
                    System.out.println("- " + name + " | 수량: " + qty + " | 금액: " + price + "원");
                }
                if (!found) {
                    System.out.println("해당 날짜에 매출 기록이 없습니다.");
                } else {
                    System.out.println("총 매출: " + total + "원");
                }
            }
        } catch (Exception e) {
            System.out.println("날짜 형식이 잘못되었습니다.");
        }
    }
}