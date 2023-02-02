package net.youssfi.bankaccountservice.mappers;

import net.youssfi.bankaccountservice.dto.response.BankAccountDTO;
import net.youssfi.bankaccountservice.entities.BankAccount;
import net.youssfi.bankaccountservice.enums.AccountStatus;
import net.youssfi.bankaccountservice.enums.AccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.beans.Customizer;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankAccountMapperImplTest {
    @InjectMocks
    private BankAccountMapperImpl underTest;
    @Mock
    private ModelMapper modelMapper;
    BankAccount givenAccount;
    BankAccountDTO givenAccountDTO;

    @BeforeEach
    void setUp() {
        Date now=new Date();
        givenAccount=BankAccount.builder()
                .id("CA1")
                .createdAt(now)
                .currency("MAD")
                .status(AccountStatus.CREATED)
                .balance(1000)
                .type(AccountType.CURRENT_ACCOUNT)
                .build();
        givenAccountDTO=BankAccountDTO.builder()
                .id("CA1")
                .createdAt(now)
                .currency("MAD")
                .status(AccountStatus.CREATED)
                .balance(1000)
                .type(AccountType.CURRENT_ACCOUNT)
                .build();
    }

    @Test
    @DisplayName("Should transfer data from an account to an AccountDTO")
    void fromAccountToAccountDTOTest() {
        when(modelMapper.map(givenAccount,BankAccountDTO.class)).thenReturn(givenAccountDTO);
        BankAccountDTO result = underTest.from(givenAccount);
        var expected=givenAccountDTO;
        assertThat(result.getId()).isEqualTo(expected.getId());
        assertThat(result.getCreatedAt()).isEqualTo(expected.getCreatedAt());
        assertThat(result.getBalance()).isEqualTo(expected.getBalance());
        assertThat(result.getType()).isEqualTo(expected.getType());
        assertThat(result.getStatus()).isEqualTo(expected.getStatus());
        assertThat(result.getCurrency()).isEqualTo(expected.getCurrency());
    }
    @Test
    @DisplayName("Should transfer data from an accountDTO to an Account")
    void fromAccountDTOToAccountDTO() {
        when(modelMapper.map(givenAccount,BankAccountDTO.class)).thenReturn(givenAccountDTO);
        BankAccountDTO result = underTest.from(givenAccount);

        var expected=givenAccount;

        assertThat(result.getId()).isEqualTo(expected.getId());
        assertThat(result.getCreatedAt()).isEqualTo(expected.getCreatedAt());
        assertThat(result.getBalance()).isEqualTo(expected.getBalance());
        assertThat(result.getType()).isEqualTo(expected.getType());
        assertThat(result.getStatus()).isEqualTo(expected.getStatus());
        assertThat(result.getCurrency()).isEqualTo(expected.getCurrency());
    }
}