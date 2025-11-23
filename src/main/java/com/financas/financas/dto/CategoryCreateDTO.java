package com.financas.financas.dto;

import com.financas.financas.model.enums.TransactionType;
import jakarta.validation.constraints.NotBlank; 
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CategoryCreateDTO {
    
    // O usuário NÃO pode enviar um nome em branco
    @NotBlank(message = "O nome é obrigatório")
    private String name;
    
    private String description;
    
    // O usuário NÃO pode enviar um tipo nulo
    @NotNull(message = "O tipo (INCOME ou EXPENSE) é obrigatório")
    private TransactionType type;
}