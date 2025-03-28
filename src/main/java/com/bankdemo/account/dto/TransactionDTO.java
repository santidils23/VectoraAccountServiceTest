package com.bankdemo.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    private Long fromAccount;
    private Long toAccount;
    private BigDecimal monto;
    private LocalDateTime fecha;
    private String status;
    private String errorMessage;
}