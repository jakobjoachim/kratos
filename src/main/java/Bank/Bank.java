package Bank;


import lombok.Data;

import java.util.List;

@Data
public class Bank {
    private String id;
    private List<BankAccount> accounts;
    private List<Transfer> transfers;
}
