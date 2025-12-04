package coffeevendingmachine.enums;

public enum Coin {

    YUAN_1(10),
    YUAN_2(20),
    JIAO_1(1),
    YUAN_5(50);

    private final int d;

    public int getD() {
        return d;
    }

    Coin(int d) {
        this.d = d;
    }
}
