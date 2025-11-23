package com.financas.financas.service;

import com.financas.financas.dto.CategoryCreateDTO;
import com.financas.financas.dto.CategoryResponseDTO;
import com.financas.financas.dto.CategoryUpdateDTO;
import com.financas.financas.model.Category;
import com.financas.financas.model.User;
import com.financas.financas.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; // Importante para o teste de Lista

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * LÓGICA DE CRIAÇÃO (CREATE)
     */
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
    
    /**
     * LÓGICA DE LEITURA (GET ALL)
     */
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getAllCategories() {
        
        // 1. Pega o usuário logado
        User user = getAuthenticatedUser();
        
        // 2. Busca TODAS as categorias deste usuário (usando o método que retorna List)
        List<Category> categoryList = categoryRepository.findByUser(user);
        
        // 3. Converte a Lista de Entidades para Lista de DTOs
        return categoryList.stream()
                           .map(CategoryResponseDTO::new)
                           .toList();
    }
    
    /**
     * LÓGICA DE LEITURA (GET ONE)
     */
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

    /**
     * LÓGICA DE ATUALIZAÇÃO (UPDATE)
     */
    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryUpdateDTO dto) {
        
        User user = getAuthenticatedUser();
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada. ID: " + id));
        
        if (!category.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Acesso negado: Esta categoria não pertence a você.");
        }

        if (dto.getName() != null) {
            category.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            category.setDescription(dto.getDescription());
        }
        if (dto.getType() != null) {
            category.setType(dto.getType());
        }
        
        return new CategoryResponseDTO(category);
    }

    /**
     * LÓGICA DE DELEÇÃO (DELETE)
     */
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

    /**
     * Método auxiliar para pegar o usuário logado
     */
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            throw new SecurityException("Nenhum usuário autenticado encontrado.");
        }
        return (User) authentication.getPrincipal();
    }
}