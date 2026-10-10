package com.ridelink.accountservice.service;

import com.ridelink.accountservice.dto.LoginRequest;
import com.ridelink.accountservice.dto.LoginResponse;
import com.ridelink.accountservice.dto.RegisterRequest;
import com.ridelink.accountservice.exception.DuplicateEmailException;
import com.ridelink.accountservice.model.Account;
import com.ridelink.accountservice.repository.AccountRepository;
import com.ridelink.accountservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AccountService accountService;

    /**
     * Registers a new account and returns a JWT login response.
     */
    public LoginResponse register(RegisterRequest request) {
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        Account account = Account.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .build();

        Account saved = accountRepository.save(account);

        String accessToken  = jwtService.generateToken(saved);
        String refreshToken = jwtService.generateRefreshToken(saved);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getExpirationMs())
                .account(accountService.toResponse(saved))
                .build();
    }

    /**
     * Authenticates an existing account and returns a JWT login response.
     */
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow();

        String accessToken  = jwtService.generateToken(account);
        String refreshToken = jwtService.generateRefreshToken(account);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getExpirationMs())
                .account(accountService.toResponse(account))
                .build();
    }
}
