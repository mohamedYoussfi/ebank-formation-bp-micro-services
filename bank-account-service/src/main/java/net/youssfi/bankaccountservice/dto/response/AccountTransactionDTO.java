package net.youssfi.bankaccountservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.youssfi.bankaccountservice.enums.TransactionType;
import java.util.Date;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AccountTransactionDTO {
    private Long id;
    private String description;
    private Date date;
    private TransactionType type;
    private double amount;
    private String accountId;
}
