package com.financas.financas.dto;

import com.financas.financas.model.Category;
import com.financas.financas.model.enums.TransactionType;
import lombok.Getter;

@Getter
public class CategoryResponseDTO {

    private Long id;
    private String name;
    private String description;
    private TransactionType type;
    private Long accessCount; 

    // Este "construtor" é um truque para converter facilmente
    // a Entidade (do banco) para este DTO (a resposta)
    public CategoryResponseDTO(Category entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.type = entity.getType();
        this.accessCount = entity.getAccessCount();
    }
}