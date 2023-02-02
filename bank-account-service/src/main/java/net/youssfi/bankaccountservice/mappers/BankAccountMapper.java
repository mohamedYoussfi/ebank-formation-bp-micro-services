package net.youssfi.bankaccountservice.mappers;

import net.youssfi.bankaccountservice.dto.response.AccountTransactionDTO;
import net.youssfi.bankaccountservice.dto.response.BankAccountDTO;
import net.youssfi.bankaccountservice.entities.AccountTransaction;
import net.youssfi.bankaccountservice.entities.BankAccount;

public interface BankAccountMapper {
    BankAccount from(BankAccountDTO bankAccountDTO);
    BankAccountDTO from(BankAccount bankAccount);
    AccountTransactionDTO from(AccountTransaction transaction);
}
