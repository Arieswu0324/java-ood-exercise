package stackoverflow.enums;

public enum ReputationCredit {
    CREATE("CREATE", 3),
    ANSWER("ANSWER", 2),
    COMMENT("COMMENT", 1),
    VOTE_UP("VOTE_UP", 1),
    VOTE_DOWN("VOTE_DOWN", -1),
    ACCEPT("ACCEPT", 5);


    private final String name;
    private final int credit;

    ReputationCredit(String name, int credit) {
        this.name = name;
        this.credit = credit;
    }

    public String getName() {
        return name;
    }

    public int getCredit() {
        return credit;
    }
}
