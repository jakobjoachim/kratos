package Bank;


import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Bank {
    private String id;
    private Map<String, BankAccount> accounts;
    private List<Transfer> transfers;
    private Map<String, Transaction> transactionMap;
}
