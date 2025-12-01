package com.financas.financas.security;

import com.financas.financas.service.UserDetailServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private UserDetailServiceImpl userDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Log simples para provar que a requisição chegou aqui
        System.out.println(">>> FILTRO ACIONADO PARA: " + request.getRequestURI());

        String token = getTokenFromRequest(request);

        if (token == null) {
            token = getTokenFromCookie(request);
        }

        if (token != null) {
            try {
                String email = jwtTokenService.getEmailFromToken(token);
                UserDetails userDetails = userDetailService.loadUserByUsername(email);

                if (jwtTokenService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    System.out.println(">>> SUCESSO: Token válido para " + email);
                }
            } catch (Exception e) {
                System.out.println(">>> ERRO: Token inválido - " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        // --- MUDANÇA: LIMPEZA AGRESSIVA DO TOKEN ---
        if (bearerToken != null) {
            // Remove a palavra "Bearer" (case insensitive) e espaços extras
            // Isso resolve problemas de "Bearer Bearer" ou falta de espaço
            return bearerToken.replace("Bearer", "").replace("bearer", "").trim();
        }
        return null;
    }
    
    private String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("auth-token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}