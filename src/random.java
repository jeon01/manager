import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class random {

    private static final String URL = "jdbc:oracle:thin:@10.10.108.126:1521/xe";
    private static final String USER = "c##manager";
    private static final String PASSWORD = "manager123";

    static {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Product 클래스 정의(필요 시 별도 파일에 둬도 됨)
    public static class Product {
        private String productName;
        private String manufacturer;
        private LocalDate expirationDate;
        private boolean isRestricted;
        private int price;
        private int stock;

        public Product(String productName, String manufacturer, LocalDate expirationDate, boolean isRestricted, int price, int stock) {
            this.productName = productName;
            this.manufacturer = manufacturer;
            this.expirationDate = expirationDate;
            this.isRestricted = isRestricted;
            this.price = price;
            this.stock = stock;
        }

        public String getProductName() { return productName; }
        public int getStock() { return stock; }
        public void setStock(int stock) { this.stock = stock; }

        @Override
        public String toString() {
            return productName + " / " + manufacturer + " / " + expirationDate + " / " +
                    (isRestricted ? "19금" : "일반") + " / " + price + "원 / 재고: " + stock + "개";
        }
    }

    // DB에서 전체 제품 불러오기
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        String sql = "SELECT product_name, manufacturer, expiration_date, is_restricted, price, stock FROM product";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Product product = new Product(
                        rs.getString("product_name"),
                        rs.getString("manufacturer"),
                        rs.getDate("expiration_date").toLocalDate(),
                        rs.getString("is_restricted").equalsIgnoreCase("Y"),
                        rs.getInt("price"),
                        rs.getInt("stock")
                );
                productList.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }

    // 재고 업데이트
    public void updateStock(String productName, int newStock) {
        String sql = "UPDATE product SET stock = ? WHERE product_name = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newStock);
            pstmt.setString(2, productName);
            pstmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 랜덤 입고 처리 메서드
    public void restockRandomly() {
        Random random = new Random();
        List<Product> products = getAllProducts();

        for (Product p : products) {
            int incoming = random.nextInt(20) + 1;  // 1~20개 랜덤 입고
            int newStock = p.getStock() + incoming;
            updateStock(p.getProductName(), newStock);
            System.out.println(p.getProductName() + "에 " + incoming + "개 입고. 재고: " + newStock);
        }
    }

    // 테스트용 main
    public static void main(String[] args) {
        random rs = new random();
        System.out.println("[랜덤 물품 입고 시작]");
        rs.restockRandomly();
        System.out.println("[랜덤 물품 입고 완료]");
    }
}