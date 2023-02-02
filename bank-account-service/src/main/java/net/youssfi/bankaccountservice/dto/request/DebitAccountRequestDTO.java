package net.youssfi.bankaccountservice.dto.request;

import net.youssfi.bankaccountservice.enums.AccountType;

public record DebitAccountRequestDTO(
        String accountId, double amount
){
}
