package coffeevendingmachine;

import coffeevendingmachine.entity.CoffeeMachineStateContext;
import coffeevendingmachine.entity.CoffeeTransactionResult;
import coffeevendingmachine.enums.CoffeeType;
import coffeevendingmachine.enums.Coin;
import coffeevendingmachine.enums.Ingredient;
import coffeevendingmachine.exceptions.CoffeeMachineException;
import coffeevendingmachine.exceptions.IncompleteMachineSettingException;
import coffeevendingmachine.exceptions.UnsupportedCoffeeTypeException;
import coffeevendingmachine.exceptions.UnsupportedStateException;
import coffeevendingmachine.factory.CoffeeFactory;
import coffeevendingmachine.observer.RefillObserver;
import coffeevendingmachine.state.CoffeeMachineState;
import coffeevendingmachine.state.IdleState;
import coffeevendingmachine.state.MaintenanceState;
import coffeevendingmachine.state.OutOfServiceState;
import coffeevendingmachine.strategy.FindChangeStrategy;
import coffeevendingmachine.strategy.SmallFirstStrategy;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class CoffeeMachine {

    private final Map<CoffeeType, Integer> menu = new ConcurrentHashMap<>();

    private final Map<CoffeeType, Integer> priceMap;

    private final Map<Ingredient, Integer> limit;

    private final Map<Ingredient, Integer> inventory = new ConcurrentHashMap<>();

    private volatile CoffeeMachineState coffeeMachineState;

    private volatile FindChangeStrategy changeStrategy = new SmallFirstStrategy();

    private final List<RefillObserver> observers = new CopyOnWriteArrayList<>();

    private final Map<Coin, Integer> money = new ConcurrentHashMap<>();

    private final CoffeeFactory factory = CoffeeFactory.getInstance();

    private final ReentrantLock lock = new ReentrantLock();

    public CoffeeMachine(Map<CoffeeType, Integer> priceMap, Map<Ingredient, Integer> limit) {
        this.priceMap = Collections.unmodifiableMap(priceMap);
        this.limit = Collections.unmodifiableMap(limit);
    }

    public void initialize() {
        inventory.putAll(limit);
        menu.putAll(priceMap);
    }

    public void start() throws CoffeeMachineException {

        if (inventory.isEmpty()) {
            throw new IncompleteMachineSettingException("insufficient ingredients");
        }

        if (menu.isEmpty()) {
            throw new IncompleteMachineSettingException("price setting not ready");
        }

        coffeeMachineState = new IdleState();
    }

    public void maintenance() {
        coffeeMachineState = new MaintenanceState();
    }

    public void shutdown() {
        coffeeMachineState = new OutOfServiceState();
    }

    public void setCoffeeMachineState(CoffeeMachineState coffeeMachineState) {
        this.coffeeMachineState = coffeeMachineState;
    }

    public void setChangeStrategy(FindChangeStrategy strategy) {
        this.changeStrategy = strategy;
    }

    public Map<Ingredient, Integer> getLimit() {
        return limit;
    }

    public void addNotificationObserver(RefillObserver observer) {
        observers.add(observer);
    }

    public CoffeeFactory getFactory() {
        return factory;
    }

    public FindChangeStrategy getChangeStrategy() {
        return changeStrategy;
    }

    public CoffeeTransactionResult dispense(CoffeeType type, Map<Coin, Integer> intake) throws CoffeeMachineException, InterruptedException {
        lock.lock();
        try {
            if (!menu.containsKey(type)) {
                throw new UnsupportedCoffeeTypeException(type);
            }
            CoffeeMachineStateContext context = new CoffeeMachineStateContext(menu, inventory, limit, changeStrategy, money, factory);
            CoffeeTransactionResult result = coffeeMachineState.dispense(context, type, intake);

            //notify low inventory
            List<Ingredient> ingredient = checkInventory(inventory);
            if (!ingredient.isEmpty()) {
                notifyObservers(ingredient);
            }

            //update menu
            delistMenu();

            return result;
        } catch (Exception e) {
            //TODO 这里的异常捕获太宽泛了，没有区分系统异常和业务异常
            //可以在CoffeeTransactionResult中增加异常字段
            System.out.println("Exception happened when dispensing coffee: " + e.getMessage());
            return new CoffeeTransactionResult(null, intake);
        } finally {
            lock.unlock();
        }
    }

    public Map<CoffeeType, Integer> getMenu() {
        return Collections.unmodifiableMap(menu);
    }

    private List<Ingredient> checkInventory(Map<Ingredient, Integer> inventory) {
        //TODO 这里的5应该是可配置的常量
        return inventory.entrySet().stream().filter(entry ->
                entry.getValue() <= 5
        ).map(Map.Entry::getKey).toList();
    }

    private void notifyObservers(List<Ingredient> ingredient) {
        observers.forEach(observer -> observer.onEvent(ingredient));
    }

    public void refill(Ingredient ingredient) throws UnsupportedStateException {

        lock.lock();
        try {
            CoffeeMachineStateContext context = new CoffeeMachineStateContext(menu, inventory, limit, changeStrategy, money, factory);

            coffeeMachineState.refill(context, ingredient);
            upListMenu();
        } finally {
            lock.unlock();
        }
    }

    public Map<Coin, Integer> collectMoney() throws UnsupportedStateException {
        lock.lock();
        try {
            CoffeeMachineStateContext context = new CoffeeMachineStateContext(menu, inventory, limit, changeStrategy, money, factory);
            return coffeeMachineState.collectMoney(context);
        } finally {
            lock.unlock();
        }
    }

    public Map<Coin, Integer> getMoney() {
        return money;
    }

    private void delistMenu() {
        //检查inventory，如果支持的咖啡类型还有ingredient inventory，展示在menu中，否则下架
        Set<CoffeeType> removed = new HashSet<>();

        menu.keySet().forEach(type -> {
            Map<Ingredient, Integer> recipe = CoffeeFactory.recipes.get(type);
            recipe.forEach((i, amount) -> {
                if (!inventory.containsKey(i) || inventory.get(i) < amount) {
                    removed.add(type);
                }
            });
        });

        removed.forEach(menu::remove);
    }

    private void upListMenu() {
        Set<CoffeeType> ready = new HashSet<>();

        for (CoffeeType type : CoffeeType.values()) {
            Map<Ingredient, Integer> recipe = CoffeeFactory.recipes.get(type);
            recipe.forEach((i, amount) -> {
                if (inventory.containsKey(i) && inventory.get(i) >= amount) {
                    ready.add(type);
                }
            });
        }

        ready.forEach(type -> {
            menu.put(type, priceMap.get(type));
        });


    }
}
