package com.bankdemo.account.service;

import com.bankdemo.account.dto.AccountRequestDTO;
import com.bankdemo.account.dto.AccountResponseDTO;
import java.math.BigDecimal;

public interface AccountService {

    AccountResponseDTO createAccount(AccountRequestDTO accountRequest);
    AccountResponseDTO getAccount(Long id);
    void updateBalance(Long id, BigDecimal amount);
    boolean validateAccount(Long id, BigDecimal amount);
}
