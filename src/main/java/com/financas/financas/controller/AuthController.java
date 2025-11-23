package com.financas.financas.controller;

import com.financas.financas.dto.AuthRequestDTO;
import com.financas.financas.dto.AuthResponseDTO;
import com.financas.financas.dto.UserRegisterDTO;
import com.financas.financas.model.User;
import com.financas.financas.repository.UserRepository;
import com.financas.financas.security.JwtTokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ENDPOINT DE LOGIN
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid AuthRequestDTO authDTO) {
        
        // 1. Cria o "pacote" de autenticação (email + senha)
        var usernamePassword = new UsernamePasswordAuthenticationToken(
                authDTO.getEmail(), 
                authDTO.getPassword()
        );

        // 2. O Spring Security tenta autenticar
        Authentication auth = authenticationManager.authenticate(usernamePassword);

        // 3. Se der certo, pega o User e gera o token
        User user = (User) auth.getPrincipal();
        String token = jwtTokenService.generateToken(user);

        // 4. Retorna o token
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }

    // ENDPOINT DE REGISTRO
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid UserRegisterDTO registerDTO) {
        
        // 1. Verifica se o email já existe
        if (userRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            // Retorna 409 Conflict se o email já estiver em uso
            return ResponseEntity.status(409).build();
        }

        // 2. Criptografa a senha
        String hashedPassword = passwordEncoder.encode(registerDTO.getPassword());

        // 3. Salva o novo usuário no banco
        User newUser = new User();
        newUser.setName(registerDTO.getName());
        newUser.setEmail(registerDTO.getEmail());
        newUser.setPassword(hashedPassword);
        
        userRepository.save(newUser);

        // 4. Retorna 201 Created (sem corpo)
        return ResponseEntity.status(201).build();
    }
}   