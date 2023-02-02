package net.youssfi.bankaccountservice.dto.request;

import net.youssfi.bankaccountservice.enums.AccountType;

public record SaveNewAccountRequestDTO (
    String id,
    double initialBalance,
    String currency,
    AccountType type,
    Long customerId
){
}
