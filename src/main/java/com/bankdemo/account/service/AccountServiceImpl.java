package com.bankdemo.account.service;

import com.bankdemo.account.dto.AccountRequestDTO;
import com.bankdemo.account.dto.AccountResponseDTO;
import com.bankdemo.account.exception.InsufficientBalanceException;
import com.bankdemo.account.exception.ResourceNotFoundException;
import com.bankdemo.account.model.Account;
import com.bankdemo.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public AccountResponseDTO createAccount(AccountRequestDTO accountRequest) {
        if(accountRequest.getSaldoInicial().compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientBalanceException("La cuenta no se puede crear con un balance negativo");
        }
        Account account = new Account();
        account.setNombre(accountRequest.getNombre());
        account.setSaldo(accountRequest.getSaldoInicial());

        Account savedAccount = accountRepository.save(account);

        return mapToDTO(savedAccount);
    }

    @Override
    @Cacheable(value = "accounts", key = "#id")
    public AccountResponseDTO getAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con ID: " + id));

        return mapToDTO(account);
    }

    @Override
    @Transactional
    @CacheEvict(value = "accounts", key = "#id")
    public void updateBalance(Long id, BigDecimal amount) {
        Account account = accountRepository.findByIdWithLock(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con ID: " + id));

        BigDecimal newBalance = account.getSaldo().add(amount);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientBalanceException("Saldo insuficiente para realizar la operación");
        }

        account.setSaldo(newBalance);
        accountRepository.save(account);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateAccount(Long id, BigDecimal amount) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con ID: " + id));

        // Verifica si hay saldo suficiente (sólo para débitos, es decir, si amount es negativo)
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            return account.getSaldo().add(amount).compareTo(BigDecimal.ZERO) >= 0;
        }

        return true;
    }

    private AccountResponseDTO mapToDTO(Account account) {
        AccountResponseDTO dto = new AccountResponseDTO();
        dto.setId(account.getId());
        dto.setNombre(account.getNombre());
        dto.setSaldo(account.getSaldo());
        return dto;
    }
}