package com.example.expensetracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.expensetracker.dto.AuthResponse;
import com.example.expensetracker.dto.LoginRequest;
import com.example.expensetracker.dto.RegisterRequest;
import com.example.expensetracker.entity.User;
import com.example.expensetracker.exception.ApiException;
import com.example.expensetracker.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthService(userRepository, passwordEncoder, jwtService);
    }

    @Test
    void registerHashesPasswordAndReturnsToken() {
        RegisterRequest request = new RegisterRequest("Alice", "alice@example.com", "secret1");
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });
        when(jwtService.generateToken(1L, "alice@example.com")).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertTrue(passwordEncoder.matches("secret1", captor.getValue().getPasswordHash()));
        assertEquals("jwt-token", response.token());
        assertEquals(1L, response.userId());
    }

    @Test
    void registerRejectsDuplicateEmail() {
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        ApiException ex = assertThrows(
                ApiException.class,
                () -> authService.register(new RegisterRequest("Alice", "alice@example.com", "secret1"))
        );

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void loginRejectsWrongPassword() {
        String hash = passwordEncoder.encode("correct");
        User user = new User("Alice", "alice@example.com", hash);
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(new LoginRequest("alice@example.com", "wrong"))
        );
    }
}
