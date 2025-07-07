import java.time.LocalDate;

public class Product {
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

    public String getProductName() {
        return productName;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public boolean isRestricted() {
        return isRestricted;
    }

    public int getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        if (stock >= 0) {
            this.stock = stock;
        }
    }

    public void addStock(int amount) {
        if (amount > 0) {
            this.stock += amount;
        }
    }

    public void reduceStock(int amount) {
        if (amount > 0 && stock >= amount) {
            this.stock -= amount;
        }
    }

    @Override
    public String toString() {
        return productName + " / " + manufacturer + " / " + expirationDate + " / " + (isRestricted ? "19금" : "일반") + " / " + price + "원 / 재고: " + stock + "개";
    }
}