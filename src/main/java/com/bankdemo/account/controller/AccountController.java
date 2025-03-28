package com.bankdemo.account.controller;

import com.bankdemo.account.dto.AccountRequestDTO;
import com.bankdemo.account.dto.AccountResponseDTO;
import com.bankdemo.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AccountResponseDTO> createAccount(@Valid @RequestBody AccountRequestDTO accountRequest) {
        AccountResponseDTO createdAccount = accountService.createAccount(accountRequest);
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AccountResponseDTO> getAccount(@PathVariable Long id) {
        AccountResponseDTO account = accountService.getAccount(id);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/{id}/validate")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Boolean> validateAccount(@PathVariable Long id, @RequestParam BigDecimal amount) {
        boolean isValid = accountService.validateAccount(id, amount);
        return ResponseEntity.ok(isValid);
    }
}