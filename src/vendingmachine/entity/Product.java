package vendingmachine.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class Product {
    private final String id;
    private final String name;
    private final LocalDateTime created;
    private final LocalDateTime bestBefore;


    public Product(String name) {
        this.name = name;
        created = LocalDateTime.now();
        bestBefore = LocalDateTime.now().plusDays(180);
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public LocalDateTime getBestBefore() {
        return bestBefore;
    }

}
