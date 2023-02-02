package net.youssfi.bankaccountservice.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.youssfi.bankaccountservice.enums.AccountStatus;
import net.youssfi.bankaccountservice.enums.AccountType;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class BankAccount {
    @Id
    private String id;
    private String currency;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    private double balance;
    @Enumerated(EnumType.STRING)
    private AccountType type;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    private Long customerId;
}
