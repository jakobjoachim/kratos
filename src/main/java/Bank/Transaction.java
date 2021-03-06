package Bank;

import Enums.TransactionPhase;
import Enums.TransactionStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Transaction {
    String id;
    TransactionStatus status;
    TransactionPhase phases;
    List<MoneyTransfer> transferInTransaction = new ArrayList<>();
}
