package com.bankdemo.account.controller;

import com.bankdemo.account.dto.AuthRequestDTO;
import com.bankdemo.account.dto.AuthResponseDTO;
import com.bankdemo.account.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;

    @Value("${admin.username:admin}")
    private String adminUsername;

    @Value("${admin.password:password}")
    private String adminPassword;

    @PostMapping("/token")
    public ResponseEntity<AuthResponseDTO> getToken(@RequestBody AuthRequestDTO request) {
        // Verificar directamente las credenciales de admin
        if (!adminUsername.equals(request.getUsername()) || !adminPassword.equals(request.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        // Crear un UserDetails para el admin
        UserDetails userDetails = User.builder()
                .username(adminUsername)
                .password(adminPassword)
                .authorities(List.of(
                        new SimpleGrantedAuthority("USER")
                ))
                .build();

        // Generar el token JWT
        String jwt = jwtService.generateToken(userDetails);

        // Devolver la respuesta
        return ResponseEntity.ok(AuthResponseDTO.builder().token(jwt).build());
    }
}