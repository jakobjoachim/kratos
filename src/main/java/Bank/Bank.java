package Bank;


import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Bank {
    private String id;
    private List<BankAccount> accounts;
    private List<Transfer> transfers;
    private Map<String, Transaction> transactionMap;
}
