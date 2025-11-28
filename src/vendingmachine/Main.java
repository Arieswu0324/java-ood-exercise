package vendingmachine;

import vendingmachine.entity.Product;
import vendingmachine.entity.ProductFactory;
import vendingmachine.entity.ProductStock;
import vendingmachine.enums.Money;
import vendingmachine.exception.VendingMachineException;
import vendingmachine.service.VendingMachine;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws VendingMachineException {

        List<ProductStock> list = new ArrayList<>();
        ProductStock p1 = new ProductStock("cookie", 1000, 10);
        ProductStock p2 = new ProductStock("coke", 500, 20);
        ProductStock p3 = new ProductStock("water", 180, 10);
        ProductStock p4 = new ProductStock("cigar", 1250, 5);
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);

        Map<Money, Integer> money = Map.of(Money.COIN_10_PENCE, 20, Money.COIN_50_PENCE, 10,
                Money.COIN_1_POUND, 10, Money.NOTE_5_POUND, 10, Money.NOTE_10_POUND, 10,
                Money.COIN_20_PENCE, 5, Money.NOTE_50_POUND, 1);
        VendingMachine machine = new VendingMachine(list, money);

        machine.startOperation();

        Map<String, List<Product>> stocking = machine.getAvailableProducts();
        System.out.println("-----printing goods------");
        stocking.forEach((key, value) -> {
            System.out.println("产品：" + key + " : " + value.size() + "个");
        });


        System.out.println("-----vending goods------");
        for (int i = 0; i < 5; i++) {
            Map<Money, Integer> input = new HashMap<>();
            input.put(Money.NOTE_10_POUND, 1);
            try {
                Optional<Map<Money, Integer>> change = machine.dispense("water", input);
                if (change.isPresent()) {
                    for (Map.Entry<Money, Integer> entry : change.get().entrySet()) {
                        System.out.println("找零： 面值[" + entry.getKey() + "]" + entry.getValue() + "个");
                    }
                }
                System.out.println("-------出货--------");
            } catch (VendingMachineException e) {
                System.out.println("交易失败: " + e.getMessage());
            }
        }


        stocking = machine.getAvailableProducts();
        System.out.println("-----printing goods------");
        stocking.forEach((key, value) -> {
            System.out.println("产品：" + key + " : " + value.size() + "个");
        });


        machine.enterMaintenance();

        System.out.println("-----refilling goods------");
        ProductFactory factory = ProductFactory.getInstance();
        List<Product> cookie = factory.produce("cookie", 10);
        List<Product> coke = factory.produce("coke", 5);
        List<Product> water = factory.produce("water", 10);
        List<Product> cigar = factory.produce("cigar", 6);

        List<Product> goods = new ArrayList<>();
        goods.addAll(cookie);
        goods.addAll(coke);
        goods.addAll(water);
        goods.addAll(cigar);

        List<Product> returned;

        returned = machine.refill(goods);
        if (returned != null && !returned.isEmpty()) {
            String names = returned.stream().map(Product::getName).collect(Collectors.joining(","));
            System.out.println("有未投入贩卖机的商品: " + names);
        }

        stocking = machine.getAvailableProducts();
        System.out.println("-----printing goods------");
        stocking.forEach((key, value) -> {
            System.out.println("产品：" + key + " : " + value.size() + "个");
        });


        System.out.println("-----collecting Money------");
        Map<Money, Integer> collectedMoney = machine.collectMoney();

        collectedMoney.forEach((key, value) -> {
            System.out.println("取出资金：" + key.name() + " : " + value + "个");
        });

        machine.shutdown();
        stocking = machine.getAvailableProducts();
        System.out.println("-----printing goods------");
        stocking.forEach((key, value) -> {
            System.out.println("产品：" + key + " : " + value.size() + "个");
        });

    }
}
