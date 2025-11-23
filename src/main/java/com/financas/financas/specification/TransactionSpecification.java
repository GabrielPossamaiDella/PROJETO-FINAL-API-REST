package com.financas.financas.specification;

import com.financas.financas.model.Transaction;
import com.financas.financas.model.enums.TransactionType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TransactionSpecification {

    // Método que CRIA a especificação
    public Specification<Transaction> getTransactionsByFilters(
            LocalDate startDate, 
            LocalDate endDate, 
            TransactionType type, 
            Long categoryId
    ) {
        // Começa com uma especificação "vazia" (não filtra nada)
        return (root, query, criteriaBuilder) -> {
            // 'root' é a nossa entidade Transaction
            // 'criteriaBuilder' é quem constrói as partes da consulta (ex: "igual a", "maior que")
            
            // Vamos criar uma lista de filtros (predicates)
            var predicates = criteriaBuilder.conjunction(); // Começa como um "AND" vazio

            // FILTRO 1: Por Data (startDate e endDate)
            if (startDate != null && endDate != null) {
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.between(root.get("date"), startDate, endDate)
                );
            }

            // FILTRO 2: Por Tipo (INCOME ou EXPENSE)
            if (type != null) {
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.equal(root.get("type"), type)
                );
            }

            // FILTRO 3: Por Categoria (categoryId)
            if (categoryId != null) {
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.equal(root.get("category").get("id"), categoryId)
                );
            }

            // Retorna a consulta final
            return predicates;
        };
    }
}   