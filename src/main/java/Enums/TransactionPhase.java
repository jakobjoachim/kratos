package Enums;

public enum TransactionPhase {
    Eins(1), Zwei(2), Drei(3);

    private int value;
    private TransactionPhase(int value) {
        this.value = value;
    }

    public static TransactionPhase fromInteger(int x) {
        switch (x) {
            case 1:
                return Eins;
            case 2:
                return Zwei;
            case  3:
                return Drei;

        }
        return null;
    }
}
