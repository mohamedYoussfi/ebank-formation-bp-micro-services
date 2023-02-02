package net.youssfi.bankaccountservice.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.youssfi.bankaccountservice.dto.request.*;
import net.youssfi.bankaccountservice.dto.response.AccountDetailsDTO;
import net.youssfi.bankaccountservice.dto.response.BankAccountDTO;
import net.youssfi.bankaccountservice.dto.response.GetBankStatsResponseDTO;
import net.youssfi.bankaccountservice.dto.response.TransferResponseDTO;
import net.youssfi.bankaccountservice.enums.AccountStatus;
import net.youssfi.bankaccountservice.enums.AccountType;
import net.youssfi.bankaccountservice.exception.BankAccountNotFoundException;
import net.youssfi.bankaccountservice.service.BankAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@AllArgsConstructor
public class BankAccountRestController {
    private BankAccountService bankAccountService;


    @GetMapping("/accounts")
    public List<BankAccountDTO> allAccounts(){
        return bankAccountService.getAllBankAccounts();
    }
    @GetMapping("/accountsByType")
    public List<BankAccountDTO> accountsByType(@RequestParam(name="type") AccountType type){
        return bankAccountService.getAccountsByType(type);
    }
    @GetMapping("/accountsByStatus")
    public List<BankAccountDTO> accountsByStatus(@RequestParam(name="status") AccountStatus status){
        return bankAccountService.getAccountsByStatus(status);
    }
    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<?> accountById(@PathVariable String accountId){
        try {
            BankAccountDTO account = bankAccountService.getBankAccountById(accountId);
            return ResponseEntity.ok().body(account);
        } catch (BankAccountNotFoundException e) {
            return ResponseEntity.internalServerError().body(Map.of("message",e.getMessage()));
        }
    }
    @PostMapping("/accounts")
    public ResponseEntity<?> saveNewAccount(@RequestBody SaveNewAccountRequestDTO request){
        try {
            BankAccountDTO account = bankAccountService.saveNewAccount(request);
            return ResponseEntity.ok().body(account);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message",e.getMessage()));
        }
    }
    @PutMapping ("/accounts/debit")
    public ResponseEntity<?> debitAccount(@RequestBody DebitAccountRequestDTO request){
        try {
            BankAccountDTO account = bankAccountService.debitAccount(request);
            return ResponseEntity.ok().body(account);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message",e.getMessage()));
        }
    }
    @PutMapping ("/accounts/credit")
    public ResponseEntity<?> creditAccount(@RequestBody CreditAccountRequestDTO request){
        try {
            BankAccountDTO account = bankAccountService.creditAccount(request);
            return ResponseEntity.ok().body(account);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message",e.getMessage()));
        }
    }
    @PutMapping ("/accounts/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferRequestDTO request){
        try {
            TransferResponseDTO transfer = bankAccountService.transfer(request);
            return ResponseEntity.ok().body(transfer);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message",e.getMessage()));
        }
    }
    @PutMapping ("/accounts/changeStatus")
    public ResponseEntity<?> changeStatus(@RequestBody ChangeAccountStatusRequestDTO request){
        try {
            BankAccountDTO bankAccountDTO = bankAccountService.changeStatusTo(request);
            return ResponseEntity.ok().body(bankAccountDTO);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message",e.getMessage()));
        }
    }
    @GetMapping("/accounts/stats")
    public GetBankStatsResponseDTO getTotalBalance(){
        return bankAccountService.getBankStats();
    }

    @GetMapping("/accountDetails/{accountId}")
    public ResponseEntity<?> accountDetails(@PathVariable String accountId){
        try {
            AccountDetailsDTO accountDetailsDTO = bankAccountService.accountDetails(accountId);
            return ResponseEntity.ok().body(accountDetailsDTO);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message",e.getMessage()));
        }
    }

}
