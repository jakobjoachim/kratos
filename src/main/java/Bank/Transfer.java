package Bank;

import lombok.Data;

@Data
public class Transfer {
    private String transferId;
    private String from;
    private String to;
    private int amount;
    private String reason;
}
