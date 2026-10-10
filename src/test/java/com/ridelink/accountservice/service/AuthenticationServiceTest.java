package com.ridelink.accountservice.service;

import com.ridelink.accountservice.dto.LoginRequest;
import com.ridelink.accountservice.dto.LoginResponse;
import com.ridelink.accountservice.dto.RegisterRequest;
import com.ridelink.accountservice.exception.DuplicateEmailException;
import com.ridelink.accountservice.model.Account;
import com.ridelink.accountservice.model.AccountStatus;
import com.ridelink.accountservice.model.Role;
import com.ridelink.accountservice.repository.AccountRepository;
import com.ridelink.accountservice.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationService Unit Tests")
class AuthenticationServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private Account savedAccount;

    @BeforeEach
    void setUp() {
        savedAccount = Account.builder()
                .id("acc-100")
                .firstName("John")
                .lastName("Rider")
                .email("john.rider@ridelink.com")
                .password("$2a$10$encodedPassword")
                .role(Role.PASSENGER)
                .status(AccountStatus.ACTIVE)
                .build();
    }

    // ── register ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("register: returns LoginResponse with tokens on successful registration")
    void register_withUniqueEmail_returnsLoginResponse() {
        RegisterRequest request = RegisterRequest.builder()
                .firstName("John")
                .lastName("Rider")
                .email("john.rider@ridelink.com")
                .password("SecurePass1!")
                .phoneNumber("+2348098765432")
                .build();

        given(accountRepository.existsByEmail(request.getEmail())).willReturn(false);
        given(passwordEncoder.encode(request.getPassword())).willReturn("$2a$10$encodedPassword");
        given(accountRepository.save(any(Account.class))).willReturn(savedAccount);
        given(jwtService.generateToken(savedAccount)).willReturn("access-token-123");
        given(jwtService.generateRefreshToken(savedAccount)).willReturn("refresh-token-456");
        given(jwtService.getExpirationMs()).willReturn(86400000L);
        given(accountService.toResponse(savedAccount)).willCallRealMethod();

        LoginResponse response = authenticationService.register(request);

        assertThat(response.getAccessToken()).isEqualTo("access-token-123");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token-456");
        assertThat(response.getExpiresIn()).isEqualTo(86400000L);
    }

    @Test
    @DisplayName("register: throws DuplicateEmailException when email already exists")
    void register_withDuplicateEmail_throwsDuplicateEmailException() {
        RegisterRequest request = RegisterRequest.builder()
                .email("duplicate@ridelink.com")
                .password("SecurePass1!")
                .firstName("Test")
                .lastName("User")
                .build();

        given(accountRepository.existsByEmail(request.getEmail())).willReturn(true);

        assertThatThrownBy(() -> authenticationService.register(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("duplicate@ridelink.com");

        then(accountRepository).should(never()).save(any(Account.class));
    }

    // ── login ──────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("login: returns LoginResponse with tokens on successful authentication")
    void login_withValidCredentials_returnsLoginResponse() {
        LoginRequest request = LoginRequest.builder()
                .email("john.rider@ridelink.com")
                .password("SecurePass1!")
                .build();

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(null);
        given(accountRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.of(savedAccount));
        given(jwtService.generateToken(savedAccount)).willReturn("access-token-xyz");
        given(jwtService.generateRefreshToken(savedAccount)).willReturn("refresh-token-xyz");
        given(jwtService.getExpirationMs()).willReturn(86400000L);
        given(accountService.toResponse(savedAccount)).willCallRealMethod();

        LoginResponse response = authenticationService.login(request);

        assertThat(response.getAccessToken()).isEqualTo("access-token-xyz");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
    }

    @Test
    @DisplayName("login: throws BadCredentialsException for invalid credentials")
    void login_withInvalidCredentials_throwsBadCredentialsException() {
        LoginRequest request = LoginRequest.builder()
                .email("john.rider@ridelink.com")
                .password("wrongPassword!")
                .build();

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authenticationService.login(request))
                .isInstanceOf(BadCredentialsException.class);

        then(accountRepository).should(never()).findByEmail(anyString());
    }
}
