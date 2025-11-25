package vendingmachine.entity;

import java.util.ArrayList;
import java.util.List;

public class ProductFactory {

    private static final ProductFactory INSTANCE = new ProductFactory();

    public static ProductFactory getInstance() {
        return INSTANCE;
    }

    public List<Product> produce(String name, Integer count) {
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Product p = new Product(name);
            products.add(p);
        }
        return products;
    }
}
