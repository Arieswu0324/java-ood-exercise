package vendingmachine.enums;

public enum Money {
    COIN_1_PENCE(1),
    COIN_2_PENCE(2),
    COIN_5_PENCE(5),
    COIN_10_PENCE(10),
    COIN_20_PENCE(20),
    COIN_50_PENCE(50),
    COIN_1_POUND(100),
    NOTE_1_POUND(100),
    NOTE_5_POUND(500),
    NOTE_10_POUND(1000),
    NOTE_20_POUND(2000),
    NOTE_50_POUND(5000)
    ;

    private int denomination;

    Money(int demonination) {
        this.denomination = demonination;
    }

    public int getDenomination(){
        return denomination;
    }
}
