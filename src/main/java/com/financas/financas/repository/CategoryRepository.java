package com.financas.financas.repository;

import com.financas.financas.model.Category;
import com.financas.financas.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Método que busca por usuário E suporta paginação
    Page<Category> findByUser(User user, Pageable pageable);

}