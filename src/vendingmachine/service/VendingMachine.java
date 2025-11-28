package vendingmachine.service;

import vendingmachine.entity.Product;
import vendingmachine.entity.ProductFactory;
import vendingmachine.entity.ProductStock;
import vendingmachine.entity.TransactionResult;
import vendingmachine.enums.Money;
import vendingmachine.exception.*;
import vendingmachine.state.IdleState;
import vendingmachine.state.MaintenanceState;
import vendingmachine.state.OutOfServiceState;
import vendingmachine.state.VendingMachineState;
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

    private VendingMachineState state;

    public VendingMachine(List<ProductStock> productStocks, Map<Money, Integer> funds) throws VendingMachineException {
        if (productStocks == null || productStocks.isEmpty()) {
            throw new IllegalArgumentException("Product Stock configs cannot be null");
        }
        productStocks.forEach(config -> {
            quantities.put(config.name(), config.count());
            products.put(config.name(), config.price());
        });

        state = new MaintenanceState();

        //fill products
        ProductFactory factory = ProductFactory.getInstance();
        for (Map.Entry<String, Integer> entry : quantities.entrySet()) {
            List<Product> products = factory.produce(entry.getKey(), entry.getValue());
            //refill(products);//这里调用了需要锁的方法，但构造其中通常不需要锁
            // 直接操作内部数据
            for (Product product : products) {
                String productName = product.getName();
                stocking.computeIfAbsent(productName, k -> new LinkedList<>()).add(product);
            }
        }

        this.funds.putAll(funds);
    }

    public Map<String, List<Product>> getAvailableProducts() throws VendingMachineException {
        lock.lock();
        try {
            return state.getAvailableProducts(stocking);
        } finally {
            lock.unlock();
        }
    }

    public Map<Money, Integer> collectMoney() throws VendingMachineException {

        lock.lock();
        try {

            StateContext context = new StateContext(
                    products, stocking, funds, strategy, quantities
            );
            return state.collectMoney(context);
        } finally {
            lock.unlock();
        }

    }

    public List<Product> refill(List<Product> products) throws VendingMachineException {
        lock.lock();
        try {
            StateContext context = new StateContext(
                   this.products, stocking, funds, strategy, quantities
            );
            return state.refill(context, products);
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
            StateContext context = new StateContext(
                    products, stocking, funds, strategy, quantities
            );
            TransactionResult result = state.dispense(context, product, intake);
            setState(result.nextState());
            return Optional.of(result.result());
        } finally {
            lock.unlock();
        }
    }

    public void startOperation() {
        lock.lock();
        try {
            this.state = new IdleState();
        } finally {
            lock.unlock();
        }
    }

    public void enterMaintenance() {
        lock.lock();
        try {
            this.state = new MaintenanceState();
        } finally {
            lock.unlock();
        }
    }

    public void shutdown() {
        lock.lock();
        try {
            this.state = new OutOfServiceState();
        } finally {
            lock.unlock();
        }
    }


    //这里不要返回对象，危险
    public String getState() {
        lock.lock();
        try {
            return this.state.getStateName();
        } finally {
            lock.unlock();
        }
    }

    private void setState(VendingMachineState state) {
        this.state = state;
    }
}