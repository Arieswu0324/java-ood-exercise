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
            return createFundSnapshot();
        } finally {
            lock.unlock();
        }
    }

    public Map<Money, Integer> collectMoney() {

        lock.lock();
        try {
            Map<Money, Integer> fundCopy = createFundSnapshot();
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

                if (!quantities.containsKey(productName)) {
                    returnedProducts.add(product); // Unknown product type
                    continue;
                }

                // Get capacity and current stock
                int capacity = quantities.get(productName);
                List<Product> currentStock = stocking.get(productName);
                int currentSize = (currentStock != null) ? currentStock.size() : 0;

                // Add if capacity allows
                if (currentSize < capacity) {
                    stocking.computeIfAbsent(productName, k -> new LinkedList<>()).add(product);
                } else {
                    returnedProducts.add(product);
                }
            }

            return returnedProducts;
        } finally {
            lock.unlock();
        }
    }

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


        lock.lock();
        try {
            if (!products.containsKey(product)) {
                throw new InvalidProductException(product);
            }

            if (!stocking.containsKey(product) || stocking.get(product).isEmpty()) {
                throw new OutOfStockException(product);
            }

            // Calculate change
            int inputMoney = calculateTotal(intake);
            int productPrice = products.get(product);
            int changeAmount = inputMoney - productPrice;

            // Insufficient intake
            if (changeAmount < 0) {
                throw new InsufficientFundsException(productPrice, inputMoney);
            }

            // Create snapshot of available funds
            TreeMap<Money, Integer> fundSnapshot = createFundSnapshot();
            // 这里又创建了一个副本，避免策略会修改快照
            TreeMap<Money, Integer> snapshotCopy = new TreeMap<>(fundSnapshot.comparator());
            snapshotCopy.putAll(funds);
            Map<Money, Integer> changePlan = strategy.findChange(changeAmount, snapshotCopy);

            //Action
            commitTransaction(product, fundSnapshot, intake, changePlan);

            return Optional.of(changePlan);

        } finally {
            lock.unlock();
        }
    }

    private TreeMap<Money, Integer> createFundSnapshot() {
        TreeMap<Money, Integer> fundSnapshot;
        if (funds.comparator() != null) {
            fundSnapshot = new TreeMap<>(funds.comparator());
        } else {
            fundSnapshot = new TreeMap<>();
        }
        fundSnapshot.putAll(funds);

        return fundSnapshot;
    }


    private void commitTransaction(String product, TreeMap<Money, Integer> fundSnapshot, Map<Money, Integer> intake, Map<Money, Integer> changePlan) throws VendingMachineException {

        TransactionSnapshot snapshot = new TransactionSnapshot(product,
                stocking.get(product).getLast(),
                fundSnapshot,
                intake,
                changePlan);

        try {
            // Commit transaction atomically
            // 1. Remove product from stock
            stocking.get(product).removeLast();
            if (stocking.get(product).isEmpty()) {
                stocking.remove(product);
            }

            // 2. Add customer payment to funds
            saveMoney(intake);

            // 3. Remove change from funds
            returnMoney(changePlan);

        } catch (Exception e) {
            rollback(snapshot);
            throw new VendingMachineException("Transaction failed and rolled back", e);
        }
    }

    private void rollback(TransactionSnapshot snapshot) {
        String productName = snapshot.product;
        stocking.computeIfAbsent(productName, k -> new LinkedList<>()).add(snapshot.outProduct());
        funds.clear();
        funds.putAll(snapshot.fundsBefore());
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


    //Memento Pattern
    private record TransactionSnapshot(String product, Product outProduct,
                                       TreeMap<Money, Integer> fundsBefore,
                                       Map<Money, Integer> intake,
                                       Map<Money, Integer> changePlan) {
    }
}