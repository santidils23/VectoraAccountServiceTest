package com.bankdemo.account.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AccountResponseDTO {
    private Long id;
    private String nombre;
    private BigDecimal saldo;
}

