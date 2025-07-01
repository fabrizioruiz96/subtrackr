package com.example.subtrackr.security.jwt;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.subtrackr.service.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain)
        throws ServletException, IOException {

        // Prendi l'header Authorization
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        
        // Logga l'header Auth ad ogni richiesta
        log.debug("[JWT Filter] Authorization header: {}", authHeader);
            
        // Controlla che esista e che inizi con "Bearer"
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        // Estrai l'username dal token (cattura eccezione se scaduto)
        username = jwtService.extractUsername(jwt);
        log.debug("[JWT Filter] Extracted userEmail: {}", username);
       
        // Se non è già stato autenticato in questo contesto
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Carica dettagli utente dal db
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            // Verifica che il token sia valido
            if (jwtService.validateToken(jwt, userDetails)) {
                // Crea un Authentication token e lo setta nel contesto
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.debug("[JWT Filter] Authentication set for user: {}", username);
            } else {
                log.debug("[JWT Filter] Token non valido");
            }
        }

        // Prosegui la catena di filtri
        filterChain.doFilter(request, response);
    }
}
