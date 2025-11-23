package com.financas.financas.repository;

import com.financas.financas.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Importe

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Método que o Spring Security vai usar para buscar o usuário pelo email
    Optional<User> findByEmail(String email);
    
}