package Bank;

import lombok.Data;

@Data
public class MoneyTransfer {
    private String transferId;
    private String from;
    private String to;
    private int amount;
    private String reason;
}
