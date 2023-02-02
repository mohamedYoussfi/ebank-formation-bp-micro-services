package net.youssfi.bankaccountservice.repository;

import net.youssfi.bankaccountservice.entities.BankAccount;
import net.youssfi.bankaccountservice.enums.AccountStatus;
import net.youssfi.bankaccountservice.enums.AccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class BankAccountRepositoryTest {
    @Autowired
    private BankAccountRepository bankAccountRepository;
    Date now;
    @BeforeEach
    void setUp() {
        now=new Date();
        for (int i = 1; i <4 ; i++) {
            System.out.println("===================="+i);
            BankAccount account1=BankAccount.builder()
                    .id("CA"+i)
                    .type(AccountType.CURRENT_ACCOUNT)
                    .balance(i*1000)
                    .createdAt(now)
                    .status(AccountStatus.ACTIVATED)
                    .currency("MAD")
                    .build();
            BankAccount account2=BankAccount.builder()
                    .id("SA"+i)
                    .type(AccountType.SAVING_ACCOUNT)
                    .balance(i*1000)
                    .createdAt(now)
                    .status(AccountStatus.CREATED)
                    .currency("MAD")
                    .build();
            bankAccountRepository.save(account1);
            bankAccountRepository.save(account2);
        }
    }

    @Test
    @DisplayName("Should return 3 Saving accounts")
    void findByType_SavingAccounts() {
        List<BankAccount> savingAccounts = bankAccountRepository.findByType(AccountType.SAVING_ACCOUNT);
        assertThat(savingAccounts.size()).isEqualTo(3);
    }
    @Test
    @DisplayName("Should return 3 Saving accounts")
    void findByType_CurrentAccounts() {
        List<BankAccount> accounts = bankAccountRepository.findByType(AccountType.CURRENT_ACCOUNT);
        assertThat(accounts.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should return 3 accounts with status Activated")
    void findByActivatedStatus() {
        List<BankAccount> accounts = bankAccountRepository.findByStatus(AccountStatus.ACTIVATED);
        assertThat(accounts.size()).isEqualTo(3);
    }
    @Test
    @DisplayName("Should return 3 accounts with status Created")
    void findByCreatedStatus() {
        List<BankAccount> currentAccounts = bankAccountRepository.findByStatus(AccountStatus.CREATED);
        assertThat(currentAccounts.size()).isEqualTo(3);
    }

    @Test
    void getBankStats() {
        AccountStats expected=new AccountStats(6,12000,2000,1000,3000);
        AccountStats bankStats = bankAccountRepository.getBankStats();
        assertThat(bankStats.accountsNumber()).isEqualTo(expected.accountsNumber());
        assertThat(bankStats.totalBalance()).isEqualTo(expected.totalBalance());
        assertThat(bankStats.avgBalance()).isEqualTo(expected.avgBalance());
        assertThat(bankStats.maxBalance()).isEqualTo(expected.maxBalance());
        assertThat(bankStats.minBalance()).isEqualTo(expected.minBalance());
    }
}