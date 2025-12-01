package com.financas.financas.service;

import com.financas.financas.dto.CategoryCreateDTO;
import com.financas.financas.dto.CategoryResponseDTO;
import com.financas.financas.dto.CategoryUpdateDTO;
import com.financas.financas.model.Category;
import com.financas.financas.model.User;
import com.financas.financas.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponseDTO createCategory(CategoryCreateDTO dto) {
        User user = getAuthenticatedUser();

        Category newCategory = new Category();
        newCategory.setName(dto.getName());
        newCategory.setDescription(dto.getDescription());
        newCategory.setType(dto.getType());
        newCategory.setUser(user);
        newCategory.setAccessCount(0L);

        Category savedCategory = categoryRepository.save(newCategory);
        return new CategoryResponseDTO(savedCategory);
    }
    
    // --- PAGINAÇÃO VOLTOU AQUI ---
    @Transactional(readOnly = true)
    public Page<CategoryResponseDTO> getAllCategories(Pageable pageable) {
        
        User user = getAuthenticatedUser();
        
        // Busca PAGINADA e filtrada por usuário
        Page<Category> categoryPage = categoryRepository.findByUser(user, pageable);
        
        return categoryPage.map(CategoryResponseDTO::new);
    }
    // -----------------------------
    
    @Transactional
    public CategoryResponseDTO getOneCategory(Long id) {
        User user = getAuthenticatedUser();
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada. ID: " + id));

        if (!category.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Acesso negado: Esta categoria não pertence a você.");
        }
        
        category.setAccessCount(category.getAccessCount() + 1);
        
        return new CategoryResponseDTO(category);
    }

    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryUpdateDTO dto) {
        User user = getAuthenticatedUser();
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada. ID: " + id));
        
        if (!category.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Acesso negado: Esta categoria não pertence a você.");
        }

        if (dto.getName() != null) category.setName(dto.getName());
        if (dto.getDescription() != null) category.setDescription(dto.getDescription());
        if (dto.getType() != null) category.setType(dto.getType());
        
        return new CategoryResponseDTO(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        User user = getAuthenticatedUser();
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada. ID: " + id));

        if (!category.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Acesso negado: Esta categoria não pertence a você.");
        }
        
        if (!category.getTransactions().isEmpty()) {
             throw new IllegalStateException("Não é possível deletar a categoria pois ela está em uso por " 
                     + category.getTransactions().size() + " transações.");
        }

        categoryRepository.delete(category);
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            throw new SecurityException("Nenhum usuário autenticado encontrado.");
        }
        return (User) authentication.getPrincipal();
    }
}