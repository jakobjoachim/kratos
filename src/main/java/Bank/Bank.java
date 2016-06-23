package Bank;


import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Bank {
    private String id;
    private Map<String, BankAccount> accounts = new HashMap<>();
    private List<MoneyTransfer> transfers = new ArrayList<>();
    private Map<String, Transaction> transactionMap = new HashMap<>();

}
