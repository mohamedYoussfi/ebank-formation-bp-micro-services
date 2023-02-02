package net.youssfi.bankaccountservice.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.youssfi.bankaccountservice.dto.request.*;
import net.youssfi.bankaccountservice.dto.response.BankAccountDTO;
import net.youssfi.bankaccountservice.dto.response.GetBankStatsResponseDTO;
import net.youssfi.bankaccountservice.dto.response.TransferResponseDTO;
import net.youssfi.bankaccountservice.enums.AccountStatus;
import net.youssfi.bankaccountservice.enums.AccountType;
import net.youssfi.bankaccountservice.exception.BalanceNotSufficientException;
import net.youssfi.bankaccountservice.exception.BankAccountNotFoundException;
import net.youssfi.bankaccountservice.service.BankAccountService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@WebMvcTest
class BankAccountRestControllerIntegrationTest {
    @MockBean
    private BankAccountService bankAccountService;
    @Autowired
    private MockMvc mockMvc;

    List<BankAccountDTO> accountsList;
    ObjectMapper objectMapper=new ObjectMapper();
    @BeforeEach
    void setUp() {
        accountsList=List.of(
                BankAccountDTO.builder().id("CA1").type(AccountType.CURRENT_ACCOUNT).balance(8000).status(AccountStatus.CREATED).createdAt(new Date()).currency("MAD").build(),
                BankAccountDTO.builder().id("CA2").type(AccountType.CURRENT_ACCOUNT).balance(2000).status(AccountStatus.ACTIVATED).createdAt(new Date()).currency("MAD").build(),
                BankAccountDTO.builder().id("SA1").type(AccountType.SAVING_ACCOUNT).balance(8000).status(AccountStatus.CREATED).createdAt(new Date()).currency("MAD").build(),
                BankAccountDTO.builder().id("CA1").type(AccountType.SAVING_ACCOUNT).balance(300).status(AccountStatus.ACTIVATED).createdAt(new Date()).currency("MAD").build()
        );
    }

    @Test
    void allAccounts() throws Exception {
        var givenAccounts=this.accountsList;
        Mockito.when(bankAccountService.getAllBankAccounts()).thenReturn(givenAccounts);
        mockMvc.perform(MockMvcRequestBuilders.get("/accounts"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(givenAccounts.size())));

        //RequestBuilder request = MockMvcRequestBuilders.get("/accounts");
        //MvcResult result = mockMvc.perform(request).andReturn();
        //Assertions.assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void accountsByType() throws Exception {
        var givenAccounts=this.accountsList.stream().filter(acc->acc.getType().equals(AccountType.CURRENT_ACCOUNT)).collect(Collectors.toList());
        Mockito.when(bankAccountService.getAccountsByType(AccountType.CURRENT_ACCOUNT)).thenReturn(givenAccounts);
        mockMvc.perform(MockMvcRequestBuilders.get("/accountsByType")
                        .param("type",AccountType.CURRENT_ACCOUNT.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(givenAccounts.size())));
    }

    @Test
    void accountsByStatus() throws Exception {
        var givenAccounts=this.accountsList.stream().filter(acc->acc.getStatus().equals(AccountType.CURRENT_ACCOUNT)).collect(Collectors.toList());
        Mockito.when(bankAccountService.getAccountsByStatus(AccountStatus.ACTIVATED)).thenReturn(givenAccounts);
        mockMvc.perform(MockMvcRequestBuilders.get("/accountsByStatus")
                        .param("status",AccountStatus.ACTIVATED.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(givenAccounts.size())));
    }

    @Test
    void accountById() throws Exception {
        var expected=this.accountsList.get(0);
        Mockito.when(bankAccountService.getBankAccountById(expected.getId())).thenReturn(expected);
        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/{accountId}",expected.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(expected.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is(expected.getType().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(expected.getStatus().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance", Matchers.is(expected.getBalance())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currency", Matchers.is(expected.getCurrency())));
    }

    @Test
    void accountByIdNotFoundError() throws Exception {
        Mockito.when(bankAccountService.getBankAccountById("CA33")).thenThrow(new BankAccountNotFoundException("Account Not Found"));
        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/{accountId}","CA33"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Account Not Found")));
    }

    @Test
    void saveNewAccount() throws Exception {
        var expected=this.accountsList.get(0);
        SaveNewAccountRequestDTO requestDTO = new SaveNewAccountRequestDTO("CC1",8000, "MAD", AccountType.CURRENT_ACCOUNT,1L);
        Mockito.when(bankAccountService.saveNewAccount(requestDTO)).thenReturn(expected);
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestDTO))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(expected.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is(expected.getType().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(expected.getStatus().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance", Matchers.is(expected.getBalance())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currency", Matchers.is(expected.getCurrency())));
    }

    @Test
    void debitAccount() throws Exception {
        var expected=this.accountsList.get(0);
        DebitAccountRequestDTO requestDTO = new DebitAccountRequestDTO(expected.getId(), 900);
        Mockito.when(bankAccountService.debitAccount(requestDTO)).thenReturn(expected);
        mockMvc.perform(MockMvcRequestBuilders.put("/accounts/debit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestDTO))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(expected.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is(expected.getType().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(expected.getStatus().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance", Matchers.is(expected.getBalance())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currency", Matchers.is(expected.getCurrency())));
    }
    @Test
    @DisplayName("Should return error message")
    void debitAccountWithException() throws Exception {
        var expected=this.accountsList.get(0);
        DebitAccountRequestDTO requestDTO = new DebitAccountRequestDTO(expected.getId(), 70000);
        Mockito.when(bankAccountService.debitAccount(requestDTO)).thenThrow(new BalanceNotSufficientException("Balance Not sufficient"));
        mockMvc.perform(MockMvcRequestBuilders.put("/accounts/debit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestDTO))
                )
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",Matchers.is("Balance Not sufficient")));
    }

    @Test
    void creditAccount() throws Exception {
        var expected=this.accountsList.get(0);
        CreditAccountRequestDTO requestDTO = new CreditAccountRequestDTO(expected.getId(), 900);
        Mockito.when(bankAccountService.creditAccount(requestDTO)).thenReturn(expected);
        mockMvc.perform(MockMvcRequestBuilders.put("/accounts/credit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestDTO))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(expected.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is(expected.getType().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(expected.getStatus().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance", Matchers.is(expected.getBalance())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currency", Matchers.is(expected.getCurrency())));
    }

    @Test
    void transfer() throws Exception {
        var account1=this.accountsList.get(0);
        var account2=this.accountsList.get(1);
        double amount=900;
        TransferRequestDTO requestDTO = new TransferRequestDTO(account1.getId(),account2.getId(),amount);
        TransferResponseDTO expected=new TransferResponseDTO("Transfer Executed",account1.getId(),account2.getId(),amount);
        Mockito.when(bankAccountService.transfer(requestDTO)).thenReturn(expected);
        mockMvc.perform(MockMvcRequestBuilders.put("/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestDTO))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is(expected.message())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountIdSource", Matchers.is(expected.accountIdSource())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountIdDestination", Matchers.is(expected.accountIdDestination())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount", Matchers.is(expected.amount())));
    }

    @Test
    void changeStatus() throws Exception {
        var expected=this.accountsList.get(0);
        ChangeAccountStatusRequestDTO requestDTO = new ChangeAccountStatusRequestDTO(expected.getId(), AccountStatus.BLOCKED);
        Mockito.when(bankAccountService.changeStatusTo(requestDTO)).thenReturn(expected);
        mockMvc.perform(MockMvcRequestBuilders.put("/accounts/changeStatus")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestDTO))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(expected.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.is(expected.getType().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(expected.getStatus().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance", Matchers.is(expected.getBalance())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currency", Matchers.is(expected.getCurrency())));
    }

    @Test
    void getStatsTest() throws Exception {
        var account=this.accountsList.get(0);
        GetBankStatsResponseDTO expected=new GetBankStatsResponseDTO(12000D,4L,6000D,2000D,8000D);
        Mockito.when(bankAccountService.getBankStats()).thenReturn(expected);
        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/stats"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalBalance", Matchers.is(expected.totalBalance().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfAccounts", Matchers.equalTo(expected.numberOfAccounts().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.avgBalance", Matchers.is(expected.avgBalance().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.minBalance", Matchers.is(expected.minBalance().doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.maxBalance", Matchers.is(expected.maxBalance().doubleValue())));
    }
}