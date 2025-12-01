package com.financas.financas.controller;

import com.financas.financas.dto.CategoryCreateDTO;
import com.financas.financas.dto.CategoryResponseDTO;
import com.financas.financas.dto.CategoryUpdateDTO;
import com.financas.financas.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> create(@Valid @RequestBody CategoryCreateDTO dto) {
        CategoryResponseDTO newCategory = categoryService.createCategory(dto);
        URI uri = URI.create("/categories/" + newCategory.getId());
        return ResponseEntity.created(uri).body(newCategory);
    }

    // --- PAGINAÇÃO VOLTOU AQUI ---
    @GetMapping
    public ResponseEntity<Page<CategoryResponseDTO>> listAll(Pageable pageable) {
        Page<CategoryResponseDTO> categoryPage = categoryService.getAllCategories(pageable);
        return ResponseEntity.ok(categoryPage);
    }
    // -----------------------------

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> listOne(@PathVariable Long id) {
        CategoryResponseDTO categoryDTO = categoryService.getOneCategory(id);
        return ResponseEntity.ok(categoryDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> update(@PathVariable Long id, @RequestBody CategoryUpdateDTO dto) {
        CategoryResponseDTO updatedCategory = categoryService.updateCategory(id, dto);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}