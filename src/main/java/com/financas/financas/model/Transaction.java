package com.financas.financas.model;

import com.financas.financas.model.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal; 
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2) // Ex: 12345678.90
    private BigDecimal amount; 

    @Column(nullable = false)
    private LocalDate date; // Melhor que 'Date' para datas sem hora

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    // Relacionamento: Muitas transações (N) pertencem a um usuário (1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Relacionamento: Muitas transações (N) pertencem a uma categoria (1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}