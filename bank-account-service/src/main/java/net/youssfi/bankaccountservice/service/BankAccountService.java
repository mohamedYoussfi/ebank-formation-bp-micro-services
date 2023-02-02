package net.youssfi.bankaccountservice.service;
import net.youssfi.bankaccountservice.dto.request.*;
import net.youssfi.bankaccountservice.dto.response.*;
import net.youssfi.bankaccountservice.enums.AccountStatus;
import net.youssfi.bankaccountservice.enums.AccountType;
import net.youssfi.bankaccountservice.exception.*;

import java.util.List;

public interface BankAccountService {
    BankAccountDTO saveNewAccount(SaveNewAccountRequestDTO requestDTO) throws AmountRejectedException, RemoteCustomerNotFoundException;

    List<BankAccountDTO> getAllBankAccounts();
    List<BankAccountDTO> getAccountsByType(AccountType type);
    List<BankAccountDTO> getAccountsByStatus(AccountStatus status);

    BankAccountDTO getBankAccountById(String accountId) throws BankAccountNotFoundException;

    BankAccountDTO debitAccount(DebitAccountRequestDTO debitAccountRequest) throws BankAccountNotFoundException, BalanceNotSufficientException, AmountRejectedException;

    BankAccountDTO creditAccount(CreditAccountRequestDTO creditAccountRequest) throws BankAccountNotFoundException, BalanceNotSufficientException, AmountRejectedException;

    TransferResponseDTO transfer(TransferRequestDTO transferRequestDTO) throws BankAccountNotFoundException, BalanceNotSufficientException, AmountRejectedException, TransferRejectedException;

    BankAccountDTO changeStatusTo(ChangeAccountStatusRequestDTO changeAccountStatusRequest) throws BankAccountNotFoundException;

    GetBankStatsResponseDTO getBankStats();
    AccountDetailsDTO accountDetails(String accountId) throws BankAccountNotFoundException;
}