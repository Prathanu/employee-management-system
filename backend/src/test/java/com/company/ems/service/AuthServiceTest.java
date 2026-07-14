package com.company.ems.service;

import com.company.ems.dto.LoginRequest;
import com.company.ems.dto.LoginResponse;
import com.company.ems.exception.InvalidCredentialsException;
import com.company.ems.entity.User;
import com.company.ems.repository.UserRepository;
import com.company.ems.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private TokenService tokenService;

    @InjectMocks private AuthService authService;

    @Test
    void login_shouldReturnToken_whenCredentialsValid() {
        var user = TestDataFactory.adminUser();
        var request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("admin123", user.getPassword())).thenReturn(true);
        when(tokenService.generateToken("admin", "ADMIN")).thenReturn("test-token");

        LoginResponse response = authService.login(request);

        assertEquals("test-token", response.getToken());
        assertEquals("admin", response.getUsername());
        assertEquals("ADMIN", response.getRole());
        verify(tokenService).generateToken(eq("admin"), eq("ADMIN"));
    }

    @Test
    void login_shouldThrow_whenUserNotFound() {
        var request = new LoginRequest();
        request.setUsername("unknown");
        request.setPassword("pass");

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void login_shouldThrow_whenPasswordInvalid() {
        var user = TestDataFactory.adminUser();
        var request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("wrong");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", user.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }
}
