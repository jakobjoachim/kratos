package Bank;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class MoneyTransfer {
    String transferId;
    @NonNull private String from;
    @NonNull private String to;
    @NonNull private int amount;
    private String reason;
}
