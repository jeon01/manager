import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class randomDAO {

    private static final Random random = new Random();

    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        String sql = "SELECT product_name, manufacturer, expiration_date, is_restricted, price, stock FROM product";

        try (Connection conn = DBUtil.getConnection();
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

    public void updateStock(String productName, int newStock) {
        String sql = "UPDATE product SET stock = ? WHERE product_name = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newStock);
            pstmt.setString(2, productName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void restockRandomly() {
        List<Product> products = getAllProducts();

        for (Product p : products) {
            int incoming = random.nextInt(20) + 1;
            int newStock = p.getStock() + incoming;
            updateStock(p.getProductName(), newStock);
            System.out.println(p.getProductName() + " 입고: " + incoming + "개, 총 재고: " + newStock);
        }
    }
}