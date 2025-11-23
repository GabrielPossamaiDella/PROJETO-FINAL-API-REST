package com.financas.financas.dto;

import com.financas.financas.model.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class TransactionCreateDTO {

    @NotBlank(message = "A descrição é obrigatória")
    private String description;

    @NotNull(message = "O valor é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    private BigDecimal amount;

    @NotNull(message = "A data é obrigatória")
    private LocalDate date;

    @NotNull(message = "O tipo (INCOME ou EXPENSE) é obrigatório")
    private TransactionType type;

    @NotNull(message = "O ID da categoria é obrigatório")
    private Long categoryId;
}