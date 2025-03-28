package com.bankdemo.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AccountRequestDTO {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotNull(message = "El saldo inicial no puede ser nulo")
    @Positive(message = "El saldo inicial debe ser positivo")
    private BigDecimal saldoInicial;
}
