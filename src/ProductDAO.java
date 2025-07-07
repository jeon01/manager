import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

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

    public void insertProduct(Product product) {
        String sql = """
            INSERT INTO product (
                product_name, manufacturer, expiration_date, is_restricted, price, stock
            ) VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getProductName());
            pstmt.setString(2, product.getManufacturer());
            pstmt.setDate(3, Date.valueOf(product.getExpirationDate()));
            pstmt.setBoolean(4, product.isRestricted());
            pstmt.setInt(5, product.getPrice());
            pstmt.setInt(6, product.getStock());

            pstmt.executeUpdate();
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        String sql = "SELECT * FROM product";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Product product = new Product(
                        rs.getString("product_name"),
                        rs.getString("manufacturer"),
                        rs.getDate("expiration_date").toLocalDate(),
                        rs.getBoolean("is_restricted"),
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
}