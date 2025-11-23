package com.financas.financas.controller;

import com.financas.financas.dto.TransactionCreateDTO;
import com.financas.financas.dto.TransactionResponseDTO;
import com.financas.financas.dto.TransactionUpdateDTO;
import com.financas.financas.model.enums.TransactionType;
import com.financas.financas.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.time.LocalDate;
import java.util.List; 

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> create(@Valid @RequestBody TransactionCreateDTO dto) {
        TransactionResponseDTO newTransaction = transactionService.createTransaction(dto);
        URI uri = URI.create("/transactions/" + newTransaction.getId());
        return ResponseEntity.created(uri).body(newTransaction);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> listAll(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Long categoryId
    ) {
        
        List<TransactionResponseDTO> transactionList = transactionService.getAllTransactions(
                startDate, endDate, type, categoryId
        );
        
        return ResponseEntity.ok(transactionList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> update(
            @PathVariable Long id, 
            @Valid @RequestBody TransactionUpdateDTO dto
    ) {
        TransactionResponseDTO updatedTransaction = transactionService.updateTransaction(id, dto);
        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}