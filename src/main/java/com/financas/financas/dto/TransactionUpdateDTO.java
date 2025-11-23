package com.financas.financas.dto;

import com.financas.financas.model.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class TransactionUpdateDTO {

    // Na atualização, o usuário pode querer mudar qualquer um destes campos
    
    private String description;

    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    private BigDecimal amount;

    private LocalDate date;

    private TransactionType type;

    private Long categoryId;
}