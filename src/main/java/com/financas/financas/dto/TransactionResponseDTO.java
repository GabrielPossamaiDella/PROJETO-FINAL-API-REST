package com.financas.financas.dto;

import com.financas.financas.model.Transaction;
import com.financas.financas.model.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class TransactionResponseDTO {

    private Long id;
    private String description;
    private BigDecimal amount;
    private LocalDate date;
    private TransactionType type;
    private CategorySimpleDTO category; 

    // Construtor que converte a Entidade para este DTO
    public TransactionResponseDTO(Transaction entity) {
        this.id = entity.getId();
        this.description = entity.getDescription();
        this.amount = entity.getAmount();
        this.date = entity.getDate();
        this.type = entity.getType();
        // para um DTO simples que vive dentro deste DTO.
        this.category = new CategorySimpleDTO(
            entity.getCategory().getId(),
            entity.getCategory().getName()
        );
    }

    // DTO interno simples para a categoria
    @Getter
    private static class CategorySimpleDTO {
        private Long id;
        private String name;

        public CategorySimpleDTO(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}