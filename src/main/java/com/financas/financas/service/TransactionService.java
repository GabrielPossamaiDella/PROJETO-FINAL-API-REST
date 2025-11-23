package com.financas.financas.service;

import com.financas.financas.dto.TransactionCreateDTO;
import com.financas.financas.dto.TransactionResponseDTO;
import com.financas.financas.dto.TransactionUpdateDTO;
import com.financas.financas.model.Category;
import com.financas.financas.model.Transaction;
import com.financas.financas.model.User;
import com.financas.financas.model.enums.TransactionType;
import com.financas.financas.repository.CategoryRepository;
import com.financas.financas.repository.TransactionRepository;
import com.financas.financas.specification.TransactionSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List; // Importe java.util.List

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private TransactionSpecification transactionSpecification;

    @Transactional
    public TransactionResponseDTO createTransaction(TransactionCreateDTO dto) {
        User user = getAuthenticatedUser();

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Categoria de ID " + dto.getCategoryId() + " não encontrada."));

        if (!category.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Acesso negado: Esta categoria não pertence a você.");
        }
        
        Transaction newTransaction = new Transaction();
        newTransaction.setDescription(dto.getDescription());
        newTransaction.setAmount(dto.getAmount());
        newTransaction.setDate(dto.getDate());
        newTransaction.setType(dto.getType());
        newTransaction.setCategory(category);
        newTransaction.setUser(user);

        Transaction savedTransaction = transactionRepository.save(newTransaction);
        return new TransactionResponseDTO(savedTransaction);
    }

    /**
     
     */
    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getAllTransactions( 
            
            LocalDate startDate, 
            LocalDate endDate, 
            TransactionType type, 
            Long categoryId
    ) {
        User user = getAuthenticatedUser();
        
        // 1. Cria os filtros normais (data, tipo, categoria)
        Specification<Transaction> spec = transactionSpecification.getTransactionsByFilters(
                startDate, endDate, type, categoryId
        );
        
        // 2. Adiciona o filtro de USUÁRIO (Segurança)
        Specification<Transaction> userSpec = (root, query, cb) -> cb.equal(root.get("user").get("id"), user.getId());
        Specification<Transaction> finalSpec = spec.and(userSpec);
        
        // 3. Busca a LISTA 
        List<Transaction> transactionList = transactionRepository.findAll(finalSpec);
        
        // 4. Converte para DTOs
        return transactionList.stream()
                .map(TransactionResponseDTO::new)
                .toList();
    }
    
    @Transactional
    public TransactionResponseDTO updateTransaction(Long id, TransactionUpdateDTO dto) {
        User user = getAuthenticatedUser();
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transação não encontrada. ID: " + id));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Acesso negado: Esta transação não pertence a você.");
        }

        if (dto.getDescription() != null) transaction.setDescription(dto.getDescription());
        if (dto.getAmount() != null) transaction.setAmount(dto.getAmount());
        if (dto.getDate() != null) transaction.setDate(dto.getDate());
        if (dto.getType() != null) transaction.setType(dto.getType());
        
        if (dto.getCategoryId() != null) {
            Category newCategory = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Nova Categoria de ID " + dto.getCategoryId() + " não encontrada."));
            if(!newCategory.getUser().getId().equals(user.getId())) {
                 throw new AccessDeniedException("Acesso negado: A nova categoria não pertence a você.");
            }
            transaction.setCategory(newCategory);
        }
        
        return new TransactionResponseDTO(transaction);
    }

    @Transactional
    public void deleteTransaction(Long id) {
        User user = getAuthenticatedUser();
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transação não encontrada. ID: " + id));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Acesso negado: Esta transação não pertence a você.");
        }
        
        transactionRepository.deleteById(id);
    }
    
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            throw new SecurityException("Nenhum usuário autenticado encontrado.");
        }
        return (User) authentication.getPrincipal();
    }
}