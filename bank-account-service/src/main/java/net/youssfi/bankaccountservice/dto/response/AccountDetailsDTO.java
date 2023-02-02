package net.youssfi.bankaccountservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AccountDetailsDTO {
    private String accountId;
    private double balance;
    private Long customerId;
    private String customerFirstName;
    private String customerLastName;
    private String customerEmail;
    private List<AccountTransactionDTO> transactions;
}
