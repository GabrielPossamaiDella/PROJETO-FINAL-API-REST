package com.financas.financas.model;

import com.financas.financas.model.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING) // Salva no banco como "INCOME" ou "EXPENSE"
    @Column(nullable = false)
    private TransactionType type;

    // --- CARTA-DESAFIO: Ranking de Popularidade ---
    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long accessCount = 0L;

    // Relacionamento: Muitas categorias (N) pertencem a um usuário (1)
    @ManyToOne(fetch = FetchType.LAZY) // LAZY = Só carrega o usuário se você pedir
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Relacionamento: Uma categoria (1) tem muitas transações (N)
    @OneToMany(mappedBy = "category")
    private List<Transaction> transactions;
}