package com.ridelink.accountservice.service;

import com.ridelink.accountservice.dto.*;
import com.ridelink.accountservice.exception.AccountNotFoundException;
import com.ridelink.accountservice.exception.InvalidPasswordException;
import com.ridelink.accountservice.model.Account;
import com.ridelink.accountservice.model.AccountStatus;
import com.ridelink.accountservice.model.Role;
import com.ridelink.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    // ── Profile ────────────────────────────────────────────────────────────────

    /**
     * Returns the account details for a given account ID.
     */
    public AccountResponse getAccountById(String id) {
        Account account = findById(id);
        return toResponse(account);
    }

    /**
     * Returns the account details for a given email address.
     */
    public AccountResponse getAccountByEmail(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AccountNotFoundException("email", email));
        return toResponse(account);
    }

    /**
     * Updates mutable profile fields (name, phone) for the given account.
     */
    public AccountResponse updateProfile(String id, UpdateProfileRequest request) {
        Account account = findById(id);

        if (request.getFirstName() != null) {
            account.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            account.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            account.setPhoneNumber(request.getPhoneNumber());
        }

        return toResponse(accountRepository.save(account));
    }

    /**
     * Changes the password for the given account after validating the current password.
     */
    public void changePassword(String id, ChangePasswordRequest request) {
        Account account = findById(id);

        if (!passwordEncoder.matches(request.getCurrentPassword(), account.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidPasswordException("New password and confirm password do not match");
        }

        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(account);
    }

    /**
     * Deletes the account with the given ID.
     */
    public void deleteAccount(String id) {
        Account account = findById(id);
        accountRepository.delete(account);
    }

    // ── Admin operations ───────────────────────────────────────────────────────

    /**
     * Returns all accounts in the system.
     */
    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Returns all accounts with a given role.
     */
    public List<AccountResponse> getAccountsByRole(Role role) {
        return accountRepository.findByRole(role)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Returns all accounts with a given status.
     */
    public List<AccountResponse> getAccountsByStatus(AccountStatus status) {
        return accountRepository.findByStatus(status)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Admin operation to change the status of an account.
     */
    public AccountResponse changeStatus(String id, ChangeStatusRequest request) {
        Account account = findById(id);
        account.setStatus(request.getStatus());
        return toResponse(accountRepository.save(account));
    }

    /**
     * Admin operation to change the role of an account.
     */
    public AccountResponse changeRole(String id, ChangeRoleRequest request) {
        Account account = findById(id);
        account.setRole(request.getRole());
        return toResponse(accountRepository.save(account));
    }

    // ── Mapper ─────────────────────────────────────────────────────────────────

    /**
     * Converts an {@link Account} document to a safe {@link AccountResponse} DTO.
     */
    public AccountResponse toResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .email(account.getEmail())
                .phoneNumber(account.getPhoneNumber())
                .role(account.getRole())
                .status(account.getStatus())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    private Account findById(String id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("id", id));
    }
}
