package net.youssfi.bankaccountservice.repository;

import net.youssfi.bankaccountservice.entities.AccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface AccountTransactionRepository extends JpaRepository<AccountTransaction,Long> {
   List<AccountTransaction> findByBankAccountId(String accountId);
}
