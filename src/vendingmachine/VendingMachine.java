package vendingmachine;

import vendingmachine.entity.Product;
import vendingmachine.entity.ProductFactory;
import vendingmachine.entity.ProductStock;
import vendingmachine.enums.Money;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

public class VendingMachine {

    private final Map<String, Integer> quantities = new HashMap<>();

    private final Map<String, Integer> products = new HashMap<>();

    private final Map<String, List<Product>> stocking = new ConcurrentHashMap<>();

    private final ConcurrentSkipListMap<Money, Integer> funds = new ConcurrentSkipListMap<>(new Comparator<Money>() {
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
            quantities.put(config.getName(), config.getCount());
            products.put(config.getName(), config.getPrice());
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
        return stocking;
    }

    public Map<Money, Integer> collectMoney() {
        return funds;
    }

    public List<Product> refill(List<Product> products) {
        lock.lock();
        try {
            List<Product> returnedProducts = new ArrayList<>();
            products.forEach(product -> {
                if (!quantities.containsKey(product.getName())) {
                    returnedProducts.add(product);//不存在的不可加入
                } else {
                    int count = quantities.get(product.getName());
                    if (!stocking.containsKey(product.getName()) || stocking.get(product.getName()).size() + 1 <= count) {
                        stocking.computeIfAbsent(product.getName(), k -> new CopyOnWriteArrayList<>()).add(product);
                    } else {
                        returnedProducts.add(product);//满了的不可加入
                    }
                }
            });
            return returnedProducts;
        } finally {
            lock.unlock();
        }
    }


    public Optional<Map<Money, Integer>> dispense(String product, Map<Money, Integer> intake) {
        if (intake == null || intake.isEmpty()) {
            throw new IllegalArgumentException("no money accepted");
        }

        //先查库存
        lock.lock();
        AtomicReference<Product> item = new AtomicReference<>();
        try {
            if (!stocking.containsKey(product)) {
                throw new RuntimeException("product: " + product + " out of stock, unable to buy");
            } else {
                stocking.computeIfPresent(product, (k, v) -> {
                            item.set(v.removeLast());
                            return v;
                        }
                );
            }

        } catch (Throwable e) {

            throw new RuntimeException(e);

        } finally {
            lock.unlock();
        }

        //先收钱再找零
        saveMoney(intake);
        Map<Money, Integer> output = null;

        int inputMoney = 0;
        for (Map.Entry<Money, Integer> entry : intake.entrySet()) {
            inputMoney += entry.getKey().getDenomination() * entry.getValue();
        }
        int change = inputMoney - products.get(product);

        if (change != 0) {
            lock.lock();
            try {
                output = findChange(change);
            } catch (Throwable e) {
                //零钱找不开
                //还货
                stocking.computeIfPresent(product, (k, v) -> {
                    v.add(item.get());
                    return v;
                });

                //还钱
                returnMoney(intake);
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
        return Optional.ofNullable(output);
    }

    private Map<Money, Integer> findChange(int change) {
        Map<Money, Integer> output = new HashMap<>();

        while (change > 0) {
            boolean find = false;
            for (Map.Entry<Money, Integer> entry : funds.entrySet()) {
                if (entry.getKey().getDenomination() <= change) {
                    find = true;
                    output.put(entry.getKey(), output.getOrDefault(entry.getKey(), 0) + 1);
                    funds.computeIfPresent(entry.getKey(), (k, v) -> {
                        v = v - 1;
                        if (v == 0) {
                            funds.remove(entry.getKey());//如果最后一个面值的钞票被取出，从fund里移除
                        }
                        return v;
                    });

                    change -= entry.getKey().getDenomination();
                    break;
                }
            }
            if (!find) {
                //取出去的钱要回填
                saveMoney(output);
                output.clear();
                throw new RuntimeException("insufficient fund to return change!");
            }

        }
        return output;
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
        for (Map.Entry<Money, Integer> entry : money.entrySet()) {
            funds.computeIfPresent(entry.getKey(), (k, v) -> {
                v = v - 1;
                if (v == 0) {
                    funds.remove(entry.getKey());//如果最后一个面值的钞票被取出，从fund里移除
                }
                return v;
            });
        }

    }
}