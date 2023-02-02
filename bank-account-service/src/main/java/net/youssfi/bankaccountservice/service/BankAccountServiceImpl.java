package net.youssfi.bankaccountservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.youssfi.bankaccountservice.dto.request.*;
import net.youssfi.bankaccountservice.dto.response.*;
import net.youssfi.bankaccountservice.entities.AccountTransaction;
import net.youssfi.bankaccountservice.entities.BankAccount;
import net.youssfi.bankaccountservice.enums.AccountStatus;
import net.youssfi.bankaccountservice.enums.AccountType;
import net.youssfi.bankaccountservice.enums.TransactionType;
import net.youssfi.bankaccountservice.exception.*;
import net.youssfi.bankaccountservice.feign.CustomerRestClient;
import net.youssfi.bankaccountservice.mappers.BankAccountMapper;
import net.youssfi.bankaccountservice.repository.AccountStats;
import net.youssfi.bankaccountservice.repository.AccountTransactionRepository;
import net.youssfi.bankaccountservice.repository.BankAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {
    private BankAccountRepository bankAccountRepository;
    private AccountTransactionRepository accountTransactionRepository;
    private BankAccountMapper bankAccountMapper;
    private CustomerRestClient customerRestClient;

    @Override
    public BankAccountDTO saveNewAccount(SaveNewAccountRequestDTO requestDTO) throws AmountRejectedException, RemoteCustomerNotFoundException {
        if(requestDTO.initialBalance()<=0){
            throw new AmountRejectedException(String.format("Insufficient Initial Balance Exception : %s ",requestDTO.initialBalance()));
        }
        CustomerResponseDTO customerResponseDTO=customerRestClient.findCustomerById(requestDTO.customerId());
        if(customerResponseDTO==null){
            throw new RemoteCustomerNotFoundException(String.format("Customer Not Found : %s", requestDTO.customerId()));
        }
        BankAccount bankAccount=new BankAccount();
        if (requestDTO.id()==null)
          bankAccount.setId(UUID.randomUUID().toString());
        else bankAccount.setId(requestDTO.id());
        bankAccount.setCreatedAt(new Date());
        bankAccount.setStatus(AccountStatus.CREATED);
        bankAccount.setBalance(requestDTO.initialBalance());
        bankAccount.setType(requestDTO.type());
        bankAccount.setCurrency(requestDTO.currency());
        bankAccount.setCustomerId(requestDTO.customerId());
        BankAccount savedAccount = bankAccountRepository.save(bankAccount);
        BankAccountDTO accountDTO = this.bankAccountMapper.from(savedAccount);
        return accountDTO;
    }

    @Override
    public List<BankAccountDTO> getAllBankAccounts() {
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        List<BankAccountDTO> bankAccountDTOList = bankAccounts.stream().map(bankAccountMapper::from).collect(Collectors.toList());
        return bankAccountDTOList;
    }

    @Override
    public List<BankAccountDTO> getAccountsByType(AccountType type) {
        List<BankAccount> accountList = bankAccountRepository.findByType(type);
        List<BankAccountDTO> bankAccountDTOS = accountList.stream().map(bankAccountMapper::from).collect(Collectors.toList());
        return bankAccountDTOS;
    }

    @Override
    public List<BankAccountDTO> getAccountsByStatus(AccountStatus status) {
        List<BankAccount> accountList = bankAccountRepository.findByStatus(status);
        List<BankAccountDTO> bankAccountDTOS = accountList.stream().map(bankAccountMapper::from).collect(Collectors.toList());
        return bankAccountDTOS;
    }

    @Override
    public BankAccountDTO getBankAccountById(String accountId) throws BankAccountNotFoundException {
        BankAccount account=bankAccount(accountId);
        BankAccountDTO accountDTO = bankAccountMapper.from(account);
        return accountDTO;
    }

    private BankAccount bankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount account=bankAccountRepository.findById(accountId).orElse(null);
        if(account==null){
            throw new BankAccountNotFoundException(String.format("BankAccount not found %s : ",accountId));
        }
        return account;
    }

    @Override
    public BankAccountDTO debitAccount(DebitAccountRequestDTO debitAccountRequestDTO) throws BankAccountNotFoundException, BalanceNotSufficientException, AmountRejectedException {
        if(debitAccountRequestDTO.amount()<=0){
            throw new AmountRejectedException(String.format("Amount Rejected : %s ",debitAccountRequestDTO.amount()));
        }
        BankAccount account=bankAccount(debitAccountRequestDTO.accountId());
        if(account.getBalance()<debitAccountRequestDTO.amount()){
            throw new BalanceNotSufficientException(String.format("Balance not sufficient : %f => %f",debitAccountRequestDTO.amount(), account.getBalance()));
        }
        AccountTransaction accountTransaction=AccountTransaction.builder()
                .date(new Date())
                .amount(debitAccountRequestDTO.amount())
                .description(String.format("Debit of amount : %f",debitAccountRequestDTO.amount()))
                .type(TransactionType.DEBIT)
                .bankAccount(account)
                .build();
        accountTransactionRepository.save(accountTransaction);
        account.setBalance(account.getBalance()-debitAccountRequestDTO.amount());
        bankAccountRepository.save(account);
        BankAccountDTO accountDTO = bankAccountMapper.from(account);
        return accountDTO;
    }

    @Override
    public BankAccountDTO creditAccount(CreditAccountRequestDTO creditRequest) throws BankAccountNotFoundException, AmountRejectedException {
        if(creditRequest.amount()<=0){
            throw new AmountRejectedException(String.format("Amount Rejected : %s ",creditRequest.amount()));
        }
        BankAccount account=bankAccount(creditRequest.accountId());
        AccountTransaction accountTransaction=AccountTransaction.builder()
                .date(new Date())
                .amount(creditRequest.amount())
                .description(String.format("Credit of amount : %f",creditRequest.amount()))
                .type(TransactionType.CREDIT)
                .bankAccount(account)
                .build();
        accountTransactionRepository.save(accountTransaction);
        account.setBalance(account.getBalance()+creditRequest.amount());
        bankAccountRepository.save(account);
        BankAccountDTO accountDTO = bankAccountMapper.from(account);
        return accountDTO;
    }

    @Override
    public TransferResponseDTO transfer(TransferRequestDTO transferRequest) throws BankAccountNotFoundException, BalanceNotSufficientException, AmountRejectedException, TransferRejectedException {
        if(transferRequest.accountIdSource().equals(transferRequest.accountIdDestination())){
            throw new TransferRejectedException(String.format("account source %s can not be the same of account destination %s",transferRequest.accountIdSource(),transferRequest.accountIdDestination()));
        }
        debitAccount(new DebitAccountRequestDTO(transferRequest.accountIdSource(),transferRequest.amount()));
        creditAccount(new CreditAccountRequestDTO(transferRequest.accountIdDestination(),transferRequest.amount()));
        return new TransferResponseDTO("Transfer success",
                transferRequest.accountIdSource(),
                transferRequest.accountIdDestination(),
                transferRequest.amount());
    }

    @Override
    public BankAccountDTO changeStatusTo(ChangeAccountStatusRequestDTO changeAccountStatusRequest) throws BankAccountNotFoundException {
        BankAccount account=bankAccount(changeAccountStatusRequest.accountId());
        account.setStatus(changeAccountStatusRequest.status());
        bankAccountRepository.save(account);
        return bankAccountMapper.from(account);
    }


    @Override
    public GetBankStatsResponseDTO getBankStats() {
        AccountStats bankStats = bankAccountRepository.getBankStats();
        return new GetBankStatsResponseDTO(
                bankStats.totalBalance(),
                bankStats.accountsNumber(),
                bankStats.avgBalance(),
                bankStats.minBalance(),
                bankStats.maxBalance()
        );
    }

    @Override
    public AccountDetailsDTO accountDetails(String accountId) throws BankAccountNotFoundException {
        BankAccountDTO bankAccountDTO=getBankAccountById(accountId);
        CustomerResponseDTO customerResponseDTO=customerRestClient.findCustomerById(bankAccountDTO.getCustomerId());
        List<AccountTransaction> transactions = accountTransactionRepository.findByBankAccountId(accountId);
        List<AccountTransactionDTO> transactionDTOS = transactions.stream().map(tx -> bankAccountMapper.from(tx)).collect(Collectors.toList());
        return AccountDetailsDTO.builder()
                .accountId(accountId)
                .balance(bankAccountDTO.getBalance())
                .transactions(transactionDTOS)
                .customerId(bankAccountDTO.getCustomerId())
                .customerFirstName(customerResponseDTO!=null?customerResponseDTO.getFirstName():null)
                .customerLastName(customerResponseDTO!=null?customerResponseDTO.getLastName():null)
                .customerEmail(customerResponseDTO!=null?customerResponseDTO.getEmail():null)
                .build();
    }

    @PostConstruct
    public void populateData() throws Exception {
        for (int i = 1; i <=5 ; i++) { // 5 Customers
            for (int j = 1; j <=2 ; j++) { // 2 accounts for each customer
                String accountId="AC-"+i+"-"+j;
                BankAccountDTO savedAccount = this.saveNewAccount(
                        new SaveNewAccountRequestDTO(
                                accountId,
                                2000 + Math.random() * 8000,
                                Math.random() > 0.5 ? "MAD" : "USD",
                                Math.random() > 0.5 ? AccountType.SAVING_ACCOUNT : AccountType.CURRENT_ACCOUNT,
                                (long)i
                        )
                );
                for (int k = 1; k <=10 ; k++) { // 20 Transactions for each account
                    this.creditAccount(new CreditAccountRequestDTO(accountId,10000+Math.random()*10000));
                    this.debitAccount(new DebitAccountRequestDTO(accountId,2000+Math.random()*5000));
                }
            }
        }

    }
}
