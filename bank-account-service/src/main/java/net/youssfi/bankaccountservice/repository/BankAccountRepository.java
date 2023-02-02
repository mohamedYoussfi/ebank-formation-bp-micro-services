package net.youssfi.bankaccountservice.repository;

import net.youssfi.bankaccountservice.entities.BankAccount;
import net.youssfi.bankaccountservice.enums.AccountStatus;
import net.youssfi.bankaccountservice.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BankAccountRepository extends JpaRepository<BankAccount,String> {
    List<BankAccount> findByType(AccountType type);
    List<BankAccount> findByStatus(AccountStatus status);
    @Query("select " +
            "new net.youssfi.bankaccountservice.repository.AccountStats(" +
            "count(ba)," +
            "sum(ba.balance), " +
            "avg(ba.balance), " +
            "min(ba.balance), " +
            "max(ba.balance)) from BankAccount ba")
    AccountStats getBankStats();
}
