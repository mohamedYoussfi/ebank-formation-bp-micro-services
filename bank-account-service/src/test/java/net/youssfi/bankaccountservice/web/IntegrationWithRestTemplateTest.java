package net.youssfi.bankaccountservice.web;

import net.youssfi.bankaccountservice.dto.request.SaveNewAccountRequestDTO;
import net.youssfi.bankaccountservice.dto.response.BankAccountDTO;
import net.youssfi.bankaccountservice.enums.AccountStatus;
import net.youssfi.bankaccountservice.enums.AccountType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationWithRestTemplateTest {
    @LocalServerPort
    private int serverPort;
    private RestTemplate restTemplate=new RestTemplate();
    private String baseURL;

    @BeforeEach
    void setUp() {
        this.baseURL="http://localhost:"+serverPort;
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void getAllAccountsTest(){
        ResponseEntity<BankAccountDTO[]> responseEntity = restTemplate.getForEntity(baseURL + "/accounts", BankAccountDTO[].class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().length).isGreaterThanOrEqualTo(10);
    }
    @Test
    @DisplayName("should add New Account")
    void addNewAccountTest(){
        SaveNewAccountRequestDTO requestDTO=new SaveNewAccountRequestDTO("CC1",6000,"MAD", AccountType.CURRENT_ACCOUNT,1L);
        ResponseEntity<BankAccountDTO> result = restTemplate.postForEntity(baseURL + "/accounts", requestDTO, BankAccountDTO.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getStatus()).isEqualTo(AccountStatus.CREATED);
        assertThat(result.getBody().getBalance()).isEqualTo(requestDTO.initialBalance());
        assertThat(result.getBody().getType()).isEqualTo(requestDTO.type());
        assertThat(result.getBody().getId()).isNotNull();
    }
}
