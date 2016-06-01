package Bank;

import Enums.TransactionPhase;
import Enums.TransactionStatus;
import lombok.Data;

@Data
public class Transaction {
    String id;
    TransactionStatus status;
    TransactionPhase phases;
    Transfer transferInTransaction;
}
