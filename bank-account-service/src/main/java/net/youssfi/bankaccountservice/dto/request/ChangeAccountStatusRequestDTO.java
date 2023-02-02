package net.youssfi.bankaccountservice.dto.request;

import net.youssfi.bankaccountservice.enums.AccountStatus;

public record ChangeAccountStatusRequestDTO(
        String accountId, AccountStatus status
){
}
