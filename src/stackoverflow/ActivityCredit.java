package stackoverflow;

public enum ActivityCredit {
    CREATE("CREATE", 3),
    ANSWER("ANSWER", 5),
    COMMENT("COMMENT", 2),
    VOTE("VOTE", 1),
    SEARCH("SEARCH", 1);


    private final String name;
    private final int credit;

    ActivityCredit(String name, int credit) {
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
