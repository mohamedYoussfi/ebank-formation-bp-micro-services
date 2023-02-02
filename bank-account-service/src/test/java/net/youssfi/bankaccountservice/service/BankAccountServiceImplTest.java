package net.youssfi.bankaccountservice.service;

import net.youssfi.bankaccountservice.dto.request.*;
import net.youssfi.bankaccountservice.dto.response.BankAccountDTO;
import net.youssfi.bankaccountservice.dto.response.CustomerResponseDTO;
import net.youssfi.bankaccountservice.dto.response.GetBankStatsResponseDTO;
import net.youssfi.bankaccountservice.entities.AccountTransaction;
import net.youssfi.bankaccountservice.entities.BankAccount;
import net.youssfi.bankaccountservice.enums.AccountStatus;
import net.youssfi.bankaccountservice.enums.AccountType;
import net.youssfi.bankaccountservice.exception.*;
import net.youssfi.bankaccountservice.feign.CustomerRestClient;
import net.youssfi.bankaccountservice.mappers.BankAccountMapper;
import net.youssfi.bankaccountservice.repository.AccountStats;
import net.youssfi.bankaccountservice.repository.AccountTransactionRepository;
import net.youssfi.bankaccountservice.repository.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BankAccountServiceImplTest {
    @Mock
    private BankAccountRepository bankAccountRepository;
    @Mock
    private AccountTransactionRepository accountTransactionRepository;
    @Mock
    private BankAccountMapper bankAccountMapper;
    @Mock
    private CustomerRestClient customerRestClient;
    @InjectMocks
    private BankAccountServiceImpl underTest;

    Map<String,BankAccount> givenAccounts;
    Map<String,BankAccountDTO> givenAccountsDTO;
    @BeforeEach
    void setUp() {
        givenAccounts=Map.of(
                "1",BankAccount.builder().id("1").balance(2000).type(AccountType.CURRENT_ACCOUNT).status(AccountStatus.ACTIVATED).build() ,
                "2",BankAccount.builder().id("2").balance(4000).type(AccountType.SAVING_ACCOUNT).status(AccountStatus.SUSPENDED).build()
        );
        givenAccountsDTO=Map.of(
                "1",BankAccountDTO.builder().id("1").balance(2000).type(AccountType.CURRENT_ACCOUNT).status(AccountStatus.ACTIVATED).build() ,
                "2",BankAccountDTO.builder().id("2").balance(4000).type(AccountType.SAVING_ACCOUNT).status(AccountStatus.SUSPENDED).build()
        );
    }
    @Test
    void testSaveNewAccount() throws AmountRejectedException, RemoteCustomerNotFoundException {

        SaveNewAccountRequestDTO givenRequestDTO=
                new SaveNewAccountRequestDTO("CC1",4000,"MAD", AccountType.CURRENT_ACCOUNT,1L);
        BankAccount account=BankAccount.builder()
                .balance(4000)
                .status(AccountStatus.CREATED)
                .currency("MAD")
                .type(AccountType.CURRENT_ACCOUNT)
                .customerId(1L)
                .build();
        BankAccountDTO expected=BankAccountDTO.builder()
                .balance(4000)
                .status(AccountStatus.CREATED)
                .currency("MAD")
                .type(AccountType.CURRENT_ACCOUNT)
                .customerId(1L)
                .build();
        CustomerResponseDTO customerResponseDTO=CustomerResponseDTO.builder()
                .id(1L)
                .build();
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(account);
        when(bankAccountMapper.from(any(BankAccount.class))).thenReturn(expected);
        when(customerRestClient.findCustomerById(anyLong())).thenReturn(customerResponseDTO);
        BankAccountDTO result = underTest.saveNewAccount(givenRequestDTO);
        ArgumentCaptor<BankAccount> argumentCaptor=ArgumentCaptor.forClass(BankAccount.class);
        verify(bankAccountRepository).save(argumentCaptor.capture());
        BankAccount savedAccount = argumentCaptor.getValue();
        assertThat(savedAccount.getId()).isNotNull();
        assertThat(savedAccount.getCreatedAt()).isNotNull();
        assertThat(savedAccount.getBalance()).isEqualTo(givenRequestDTO.initialBalance());
        assertThat(savedAccount.getCurrency()).isEqualTo(givenRequestDTO.currency());
        assertThat(savedAccount.getType()).isEqualTo(givenRequestDTO.type());
        assertThat(savedAccount.getStatus()).isEqualTo(AccountStatus.CREATED);
        assertThat(result.getBalance()).isEqualTo(givenRequestDTO.initialBalance());
        assertThat(result.getType()).isEqualTo(givenRequestDTO.type());
        assertThat(result.getStatus()).isEqualTo(AccountStatus.CREATED);
        assertThat(result.getCurrency()).isEqualTo(givenRequestDTO.currency());
    }
    @Test
    void saveAccountWithAmountRejectedException(){
        SaveNewAccountRequestDTO givenRequestDTO=
                new SaveNewAccountRequestDTO("CC1",0,"MAD", AccountType.CURRENT_ACCOUNT, 1L);
        assertThatThrownBy(()->underTest.saveNewAccount(givenRequestDTO))
                .isInstanceOf(AmountRejectedException.class)
                .hasMessageContaining("Insufficient Initial Balance Exception");
    }

    @Test
    @DisplayName("should return all accounts")
    void getAllAccountsTest(){
        List<BankAccount> allBankAccounts=givenAccounts.values().stream().toList();
        when(bankAccountRepository.findAll()).thenReturn(allBankAccounts);
        for (BankAccount account:allBankAccounts) {
            when(bankAccountMapper.from(account)).thenReturn(givenAccountsDTO.get(account.getId()));
        }
        List<BankAccountDTO> result = underTest.getAllBankAccounts();
        assertThat(result.size()).isEqualTo(givenAccounts.size());
        for (int i = 0; i <result.size() ; i++) {
            String accountId=result.get(i).getId();
            assertThat(accountId).isEqualTo(givenAccounts.get(accountId).getId());
            assertThat(result.get(i).getBalance()).isEqualTo(givenAccounts.get(accountId).getBalance());
        }
    }
    @Test
    @DisplayName("should return saving accounts")
    void getSavingAccountsTest(){
        List<BankAccount> savingAccounts=givenAccounts.values().stream().filter(ac->ac.getType().equals(AccountType.SAVING_ACCOUNT)).collect(Collectors.toList());
        when(bankAccountRepository.findByType(AccountType.SAVING_ACCOUNT)).thenReturn(savingAccounts);
        for (BankAccount account:savingAccounts) {
            when(bankAccountMapper.from(account)).thenReturn(givenAccountsDTO.get(account.getId()));
        }
        List<BankAccountDTO> result = underTest.getAccountsByType(AccountType.SAVING_ACCOUNT);
        assertThat(result.size()).isEqualTo(savingAccounts.size());
        for (int i = 0; i <result.size() ; i++) {
            String accountId=result.get(i).getId();
            assertThat(accountId).isEqualTo(givenAccounts.get(accountId).getId());
            assertThat(result.get(i).getBalance()).isEqualTo(givenAccounts.get(accountId).getBalance());
        }
    }
    @Test
    @DisplayName("should return account by status")
    void getAccountsByStatusTest(){
        List<BankAccount> activatedAccounts=givenAccounts.values().stream().filter(ac->ac.getStatus().equals(AccountStatus.ACTIVATED)).collect(Collectors.toList());
        when(bankAccountRepository.findByStatus(AccountStatus.ACTIVATED)).thenReturn(activatedAccounts);
        for (BankAccount account:activatedAccounts) {
            when(bankAccountMapper.from(account)).thenReturn(givenAccountsDTO.get(account.getId()));
        }
        List<BankAccountDTO> result = underTest.getAccountsByStatus(AccountStatus.ACTIVATED);
        assertThat(result.size()).isEqualTo(activatedAccounts.size());
        for (BankAccountDTO bankAccount:result) {
            assertThat(bankAccount.getStatus()).isEqualTo(AccountStatus.ACTIVATED);
        }
    }
    @Test
    @DisplayName("Should debit a given account")
    void debitAccountTest() throws AmountRejectedException, BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount givenAccount=givenAccounts.get("1");
        double currentBalance=givenAccount.getBalance();
        double amount=500;
        DebitAccountRequestDTO givenRequestDTO=new DebitAccountRequestDTO(givenAccount.getId(),amount);
        when(bankAccountRepository.findById(givenRequestDTO.accountId())).thenReturn(Optional.of(givenAccount));
        when(bankAccountRepository.save(givenAccount)).thenReturn(givenAccount);
        underTest.debitAccount(givenRequestDTO);
        assertThat(givenAccount.getBalance()).isEqualTo(currentBalance-amount);
    }
    @Test
    @DisplayName("Should throw exception when debiting a given account")
    void debitAccountWithExceptionTest() throws AmountRejectedException, BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount givenAccount=givenAccounts.get("1");
        double currentBalance=givenAccount.getBalance();
        double amount=currentBalance+500;
        DebitAccountRequestDTO givenRequestDTO=new DebitAccountRequestDTO(givenAccount.getId(),amount);
        when(bankAccountRepository.findById(givenRequestDTO.accountId())).thenReturn(Optional.of(givenAccount));
        assertThatThrownBy(()->underTest.debitAccount(givenRequestDTO))
                .isInstanceOf(BalanceNotSufficientException.class)
                .hasMessageContaining(String.format("Balance not sufficient : %f => %f",givenRequestDTO.amount(), givenAccount.getBalance()));
    }
    @Test
    @DisplayName("Should throw RejectedAmount exception when debiting a given account")
    void debitAccountWithRejectedAmount() throws AmountRejectedException, BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount givenAccount=givenAccounts.get("1");
        double amount=-50;
        DebitAccountRequestDTO givenRequestDTO=new DebitAccountRequestDTO(givenAccount.getId(),amount);
        assertThatThrownBy(()->underTest.debitAccount(givenRequestDTO))
                .isInstanceOf(AmountRejectedException.class)
                .hasMessageContaining(String.format("Amount Rejected"));
    }

    @Test
    @DisplayName("Should credit a given account")
    void creditAccountTest() throws AmountRejectedException, BankAccountNotFoundException{
        BankAccount givenAccount=givenAccounts.get("1");
        double currentBalance=givenAccount.getBalance();
        double amount=500;
        CreditAccountRequestDTO givenRequestDTO=new CreditAccountRequestDTO(givenAccount.getId(),amount);
        when(bankAccountRepository.findById(givenRequestDTO.accountId())).thenReturn(Optional.of(givenAccount));
        when(bankAccountRepository.save(givenAccount)).thenReturn(givenAccount);
        underTest.creditAccount(givenRequestDTO);
        assertThat(givenAccount.getBalance()).isEqualTo(currentBalance+amount);
    }
    @Test
    @DisplayName("Should throw RejectedAmount exception when debiting a given account")
    void creditAccountWithRejectedAmount() throws AmountRejectedException, BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount givenAccount=givenAccounts.get("1");
        double amount=-50;
        CreditAccountRequestDTO creditAccountRequestDTO=new CreditAccountRequestDTO(givenAccount.getId(),amount);
        assertThatThrownBy(()->underTest.creditAccount(creditAccountRequestDTO))
                .isInstanceOf(AmountRejectedException.class)
                .hasMessageContaining(String.format("Amount Rejected"));
    }
    @Test
    @DisplayName("Should transfer a given account from an account to an other")
    void transferToAccountTest() throws AmountRejectedException, BankAccountNotFoundException, TransferRejectedException, BalanceNotSufficientException {
        BankAccount givenSourceAccount=givenAccounts.get("1");
        BankAccount givenDestinationAccount=givenAccounts.get("2");
        double currentAccountSourceBalance=givenSourceAccount.getBalance();
        double currentAccountDestinationBalance=givenDestinationAccount.getBalance();
        double amount=500;
        TransferRequestDTO transferRequestDTO=new TransferRequestDTO(givenSourceAccount.getId(),givenDestinationAccount.getId(),amount);
        when(bankAccountRepository.findById(givenSourceAccount.getId())).thenReturn(Optional.of(givenSourceAccount));
        when(bankAccountRepository.findById(givenDestinationAccount.getId())).thenReturn(Optional.of(givenDestinationAccount));
        when(bankAccountRepository.save(givenSourceAccount)).thenReturn(givenSourceAccount);
        when(bankAccountRepository.save(givenDestinationAccount)).thenReturn(givenDestinationAccount);
        when(accountTransactionRepository.save(any(AccountTransaction.class))).thenReturn(null);
        underTest.transfer(transferRequestDTO);
        assertThat(givenSourceAccount.getBalance()).isEqualTo(currentAccountSourceBalance-amount);
        assertThat(givenDestinationAccount.getBalance()).isEqualTo(currentAccountDestinationBalance+amount);
    }

    @Test
    @DisplayName("Should throw RejectedTransfer exception ")
    void transferWithRejectedTransferException() throws AmountRejectedException, BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount givenAccount=givenAccounts.get("1");
        double amount=-50;
        TransferRequestDTO transferResponseDTO=new TransferRequestDTO(givenAccount.getId(),givenAccount.getId(),amount);
        assertThatThrownBy(()->underTest.transfer(transferResponseDTO))
                .isInstanceOf(TransferRejectedException.class)
                .hasMessageContaining(String.format("account source %s can not be the same of account destination %s",transferResponseDTO.accountIdSource(),transferResponseDTO.accountIdDestination()));
    }

    @Test
    @DisplayName("Should return a given account by id")
    void getAccountByIdTest() throws BankAccountNotFoundException {
        BankAccount givenAccount=givenAccounts.get("1");
        BankAccountDTO givenAccountDTO=givenAccountsDTO.get("1");
        when(bankAccountRepository.findById(givenAccount.getId())).thenReturn(Optional.of(givenAccount));
        when(bankAccountMapper.from(givenAccount)).thenReturn(givenAccountDTO);
        BankAccountDTO result = underTest.getBankAccountById(givenAccount.getId());
        assertThat(result.getId()).isEqualTo(givenAccountDTO.getId());
        assertThat(result.getBalance()).isEqualTo(givenAccountDTO.getBalance());
        assertThat(result.getType()).isEqualTo(givenAccountDTO.getType());
    }
    @Test
    @DisplayName("Should throw not found exception when getting account by id")
    void getAccountByIdWithNotFoundExceptionTest() throws BankAccountNotFoundException {
        String accountId="3";
        when(bankAccountRepository.findById(accountId)).thenReturn(Optional.empty());
        assertThatThrownBy(()->underTest.getBankAccountById(accountId))
                .isInstanceOf(BankAccountNotFoundException.class)
                .hasMessageContaining("BankAccount not found");
    }
    @Test
    @DisplayName("Should change status")
    void changeStatusTest() throws BankAccountNotFoundException {
        BankAccount givenAccount=givenAccounts.get("1");
        AccountStatus givenAccountStatus=AccountStatus.BLOCKED;
        when(bankAccountRepository.findById(givenAccount.getId())).thenReturn(Optional.of(givenAccount));
        underTest.changeStatusTo(new ChangeAccountStatusRequestDTO(givenAccount.getId(), givenAccountStatus));
        ArgumentCaptor<BankAccount> argumentCaptor=ArgumentCaptor.forClass(BankAccount.class);
        verify(bankAccountRepository).save(argumentCaptor.capture());
        BankAccount capturedBankAccount = argumentCaptor.getValue();
        assertThat(capturedBankAccount.getStatus()).isEqualTo(givenAccountStatus);
    }
    @Test
    @DisplayName("Should return stats")
    void getBankStatsTest(){
        AccountStats givenStats=new AccountStats(6000,2,3000,2000,4000);
        when(bankAccountRepository.getBankStats()).thenReturn(givenStats);
        GetBankStatsResponseDTO result = underTest.getBankStats();
        assertThat(result.numberOfAccounts()).isEqualTo(givenStats.accountsNumber());
        assertThat(result.totalBalance()).isEqualTo(givenStats.totalBalance());
        assertThat(result.avgBalance()).isEqualTo(givenStats.avgBalance());
        assertThat(result.minBalance()).isEqualTo(givenStats.minBalance());
        assertThat(result.maxBalance()).isEqualTo(givenStats.maxBalance());
    }
}