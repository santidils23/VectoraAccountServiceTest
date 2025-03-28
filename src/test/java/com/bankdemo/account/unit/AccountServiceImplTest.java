package com.bankdemo.account.unit;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.bankdemo.account.exception.InsufficientBalanceException;
import com.bankdemo.account.exception.ResourceNotFoundException;
import com.bankdemo.account.service.AccountServiceImpl;
import com.bankdemo.account.repository.AccountRepository;
import com.bankdemo.account.dto.AccountRequestDTO;
import com.bankdemo.account.dto.AccountResponseDTO;
import com.bankdemo.account.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.util.Optional;

class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAccountWithValidData() {
        AccountRequestDTO request = new AccountRequestDTO("John Doe", BigDecimal.valueOf(1000.0));
        Account account = new Account(1L, "John Doe", BigDecimal.valueOf(1000.0));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        AccountResponseDTO response = accountService.createAccount(request);

        assertNotNull(response);
        assertEquals("John Doe", response.getNombre());
        assertEquals(BigDecimal.valueOf(1000.0), response.getSaldo());
    }

    @Test
    void testCreateAccountWithZeroBalance() {
        AccountRequestDTO request = new AccountRequestDTO("Jane Doe", BigDecimal.ZERO);
        Account account = new Account(2L, "Jane Doe", BigDecimal.ZERO);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        AccountResponseDTO response = accountService.createAccount(request);

        assertNotNull(response);
        assertEquals("Jane Doe", response.getNombre());
        assertEquals(BigDecimal.ZERO, response.getSaldo());
    }

    @Test
    void testCreateAccountWithNegativeBalanceShouldFail() {
        AccountRequestDTO request = new AccountRequestDTO("Invalid User", BigDecimal.valueOf(-500.0));

        assertThrows(InsufficientBalanceException.class, () -> accountService.createAccount(request));
    }

    @Test
    void testGetAccountByNonExistingId() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            accountService.getAccount(99L);
        });
    }
}