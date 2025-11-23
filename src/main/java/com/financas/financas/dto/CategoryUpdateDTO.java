package com.financas.financas.dto;

import com.financas.financas.model.enums.TransactionType;
import lombok.Getter;

@Getter
public class CategoryUpdateDTO {

    // Na atualização, o usuário pode querer mudar só o nome,
    // ou só a descrição, ou só o tipo.
    
    private String name;
    private String description;
    private TransactionType type;
}