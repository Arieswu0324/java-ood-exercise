package vendingmachine.entity;

public class ProductStock {
    private final String name;
    private final int price;
    private final int count;

    public ProductStock(String name, int price, int count) {
        this.name = name;
        this.price = price;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public int getPrice() {
        return price;
    }
}
