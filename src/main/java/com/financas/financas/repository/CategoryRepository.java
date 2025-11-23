package com.financas.financas.repository;

import com.financas.financas.model.Category;
import com.financas.financas.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // IMPORTE ESTE

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Page<Category> findByUser(User user, Pageable pageable);

    List<Category> findByUser(User user);

}