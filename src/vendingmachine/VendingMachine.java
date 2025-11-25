package vendingmachine;

import vendingmachine.entity.Product;
import vendingmachine.entity.ProductFactory;
import vendingmachine.entity.ProductStock;
import vendingmachine.enums.Money;
import vendingmachine.exception.*;
import vendingmachine.strategy.AtLeastOnceFindChangeStrategy;
import vendingmachine.strategy.FindChangeStrategy;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class VendingMachine {

    private final Map<String, Integer> quantities = new HashMap<>();

    private final Map<String, Integer> products = new HashMap<>();

    private final Map<String, List<Product>> stocking = new HashMap<>();

    //指定
    private final FindChangeStrategy strategy = new AtLeastOnceFindChangeStrategy();

    private final TreeMap<Money, Integer> funds = new TreeMap<>(new Comparator<Money>() {
        @Override
        public int compare(Money o1, Money o2) {
            return o2.getDenomination() - o1.getDenomination();
        }
    });

    private final ReentrantLock lock = new ReentrantLock();


    public VendingMachine(List<ProductStock> productStocks, Map<Money, Integer> funds) {
        if (productStocks == null || productStocks.isEmpty()) {
            throw new IllegalArgumentException("Product Stock configs cannot be null");
        }
        productStocks.forEach(config -> {
            quantities.put(config.name(), config.count());
            products.put(config.name(), config.price());
        });

        //fill products
        ProductFactory factory = ProductFactory.getInstance();
        for (Map.Entry<String, Integer> entry : quantities.entrySet()) {
            List<Product> products = factory.produce(entry.getKey(), entry.getValue());
            refill(products);
        }

        this.funds.putAll(funds);
    }

    public Map<String, List<Product>> getAvailableProducts() {
        lock.lock();
        try {
            Map<String, List<Product>> inventory = new HashMap<>();
            for (Map.Entry<String, List<Product>> entry : stocking.entrySet()) {
                String key = entry.getKey();

                List<Product> products = new LinkedList<>(entry.getValue());
                inventory.put(key, products);//product是不可变类，所有变量final，没有setter，所以可以直接用，否则要再拷贝product这一层
            }

            return inventory;

        } finally {
            lock.unlock();
        }
    }

    public Map<Money, Integer> getAvailableMoney() {
        lock.lock();
        try {
            TreeMap<Money, Integer> fundCopy;
            if (funds.comparator() != null) {
                fundCopy = new TreeMap<>(funds);
            } else {
                fundCopy = new TreeMap<>();
                fundCopy.putAll(funds);
            }
            return fundCopy;
        } finally {
            lock.unlock();
        }
    }

    public Map<Money, Integer> collectMoney() {

        lock.lock();
        try {
            TreeMap<Money, Integer> fundCopy;
            if (funds.comparator() != null) {
                fundCopy = new TreeMap<>(funds);
            } else {
                fundCopy = new TreeMap<>();
                fundCopy.putAll(funds);
            }
            funds.clear();
            return fundCopy;
        } finally {
            lock.unlock();
        }

    }

    public List<Product> refill(List<Product> products) {
        lock.lock();
        try {
            List<Product> returnedProducts = new ArrayList<>();

            for (Product product : products) {
                String productName = product.getName();

                // Check if product type is supported
                if (!quantities.containsKey(productName)) {
                    returnedProducts.add(product); // Unknown product type
                    continue;
                }

                // Get capacity and current stock
                int capacity = quantities.get(productName);
                List<Product> currentStock = stocking.get(productName);
                int currentSize = (currentStock != null) ? currentStock.size() : 0;

                // Check if we have room for one more item
                if (currentSize < capacity) {
                    stocking.computeIfAbsent(productName, k -> new LinkedList<>()).add(product);
                } else {
                    returnedProducts.add(product); // Machine is full for this product
                }
            }

            return returnedProducts;
        } finally {
            lock.unlock();
        }
    }


    /**
     * Dispenses a product and returns change.
     *
     * @param product Product name to dispense
     * @param intake  Money inserted by customer
     * @return Change to return (empty if exact payment)
     * @throws InvalidPaymentException     if payment is null, empty, or invalid
     * @throws InvalidProductException     if product name is not recognized
     * @throws OutOfStockException         if product is not available
     * @throws InsufficientFundsException  if payment is less than product price
     * @throws InsufficientChangeException if machine cannot make change with available denominations
     */
    public Optional<Map<Money, Integer>> dispense(String product, Map<Money, Integer> intake)
            throws VendingMachineException {

        // Validate payment
        if (intake == null || intake.isEmpty()) {
            throw new InvalidPaymentException("No payment provided");
        }

        // Validate product name
        if (product == null || product.trim().isEmpty()) {
            throw new InvalidPaymentException("Product name cannot be null or empty");
        }

        // Lock the entire transaction
        lock.lock();
        try {
            // Check product exists in configuration
            if (!products.containsKey(product)) {
                throw new InvalidProductException(product);
            }

            // Check product is in stock
            if (!stocking.containsKey(product) || stocking.get(product).isEmpty()) {
                throw new OutOfStockException(product);
            }

            // Calculate payment total
            int inputMoney = calculateTotal(intake);
            int productPrice = products.get(product);
            int changeAmount = inputMoney - productPrice;

            // Check sufficient funds provided
            if (changeAmount < 0) {
                throw new InsufficientFundsException(productPrice, inputMoney);
            }


            // Create snapshot of available funds
            TreeMap<Money, Integer> snapshot;
            if (funds.comparator() != null) {
                snapshot = new TreeMap<>(funds.comparator());
            } else {
                snapshot = new TreeMap<>();
            }
            snapshot.putAll(funds);

            Map<Money, Integer> changePlan = strategy.findChange(changeAmount, snapshot);

            // All validation passed - commit transaction atomically
            // 1. Remove product from stock
            stocking.get(product).removeLast();
            if (stocking.get(product).isEmpty()) {
                stocking.remove(product);
            }

            // 2. Add customer payment to funds
            saveMoney(intake);

            // 3. Remove change from funds
            returnMoney(changePlan);

            return Optional.of(changePlan);

        } finally {
            lock.unlock();
        }
    }


    private int calculateTotal(Map<Money, Integer> intake) {
        int inputMoney = 0;
        for (Map.Entry<Money, Integer> entry : intake.entrySet()) {
            inputMoney += entry.getKey().getDenomination() * entry.getValue();
        }
        return inputMoney;
    }


    private void saveMoney(Map<Money, Integer> money) {
        for (Map.Entry<Money, Integer> entry : money.entrySet()) {
            funds.compute(entry.getKey(), (k, v) -> {
                if (v == null) {
                    v = entry.getValue();
                } else {
                    v += entry.getValue();
                }
                return v;
            });
        }
    }

    private void returnMoney(Map<Money, Integer> money) {
        if (money.isEmpty()) {
            return;
        }
        for (Map.Entry<Money, Integer> entry : money.entrySet()) {
            funds.computeIfPresent(entry.getKey(), (k, v) -> {
                v = v - entry.getValue();
                return v <= 0 ? null : v;
            });
        }

    }
}