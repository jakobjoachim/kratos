package Broker;

import Bank.MoneyTransfer;
import Enums.TransactionPhase;
import Enums.TransactionStatus;
import lombok.Data;

@Data
public class PlaceTransaction {
    String id;
    TransactionStatus status;
    TransactionPhase phases;
    MoneyTransfer moneyTransfer;
    PlaceTransfer placeTransfer;
}
