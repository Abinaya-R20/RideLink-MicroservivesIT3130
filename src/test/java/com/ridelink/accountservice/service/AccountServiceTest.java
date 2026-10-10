package com.ridelink.accountservice.service;

import com.ridelink.accountservice.dto.*;
import com.ridelink.accountservice.exception.AccountNotFoundException;
import com.ridelink.accountservice.exception.InvalidPasswordException;
import com.ridelink.accountservice.model.Account;
import com.ridelink.accountservice.model.AccountStatus;
import com.ridelink.accountservice.model.Role;
import com.ridelink.accountservice.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountService Unit Tests")
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = Account.builder()
                .id("acc-001")
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@ridelink.com")
                .password("$2a$10$encodedPassword")
                .phoneNumber("+2348012345678")
                .role(Role.PASSENGER)
                .status(AccountStatus.ACTIVE)
                .build();
    }

    // ── getAccountById ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("getAccountById: returns AccountResponse when account exists")
    void getAccountById_whenFound_returnsResponse() {
        given(accountRepository.findById("acc-001")).willReturn(Optional.of(testAccount));

        AccountResponse response = accountService.getAccountById("acc-001");

        assertThat(response.getId()).isEqualTo("acc-001");
        assertThat(response.getEmail()).isEqualTo("jane.doe@ridelink.com");
    }

    @Test
    @DisplayName("getAccountById: throws AccountNotFoundException when account is missing")
    void getAccountById_whenNotFound_throwsException() {
        given(accountRepository.findById(anyString())).willReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getAccountById("unknown"))
                .isInstanceOf(AccountNotFoundException.class);
    }

    // ── getAccountByEmail ──────────────────────────────────────────────────────

    @Test
    @DisplayName("getAccountByEmail: returns AccountResponse for valid email")
    void getAccountByEmail_whenFound_returnsResponse() {
        given(accountRepository.findByEmail(testAccount.getEmail()))
                .willReturn(Optional.of(testAccount));

        AccountResponse response = accountService.getAccountByEmail(testAccount.getEmail());

        assertThat(response.getEmail()).isEqualTo(testAccount.getEmail());
    }

    // ── updateProfile ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("updateProfile: updates only supplied non-null fields")
    void updateProfile_updatesSuppliedFields() {
        given(accountRepository.findById("acc-001")).willReturn(Optional.of(testAccount));
        given(accountRepository.save(any(Account.class))).willAnswer(inv -> inv.getArgument(0));

        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .firstName("Janet")
                .build();

        AccountResponse response = accountService.updateProfile("acc-001", request);

        assertThat(response.getFirstName()).isEqualTo("Janet");
        assertThat(response.getLastName()).isEqualTo("Doe");  // unchanged
    }

    // ── changePassword ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("changePassword: succeeds with correct current password and matching new passwords")
    void changePassword_withValidData_succeeds() {
        given(accountRepository.findById("acc-001")).willReturn(Optional.of(testAccount));
        given(passwordEncoder.matches("correct-current", testAccount.getPassword())).willReturn(true);
        given(passwordEncoder.encode("newSecret123")).willReturn("$2a$10$newEncoded");
        given(accountRepository.save(any(Account.class))).willReturn(testAccount);

        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("correct-current")
                .newPassword("newSecret123")
                .confirmPassword("newSecret123")
                .build();

        assertThatNoException().isThrownBy(() -> accountService.changePassword("acc-001", request));
        then(accountRepository).should().save(any(Account.class));
    }

    @Test
    @DisplayName("changePassword: throws InvalidPasswordException for wrong current password")
    void changePassword_withWrongCurrentPassword_throwsException() {
        given(accountRepository.findById("acc-001")).willReturn(Optional.of(testAccount));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("wrong-password")
                .newPassword("newSecret123")
                .confirmPassword("newSecret123")
                .build();

        assertThatThrownBy(() -> accountService.changePassword("acc-001", request))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("Current password is incorrect");
    }

    @Test
    @DisplayName("changePassword: throws InvalidPasswordException when new passwords do not match")
    void changePassword_withMismatchedNewPasswords_throwsException() {
        given(accountRepository.findById("acc-001")).willReturn(Optional.of(testAccount));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("correct-current")
                .newPassword("newSecret123")
                .confirmPassword("differentSecret!")
                .build();

        assertThatThrownBy(() -> accountService.changePassword("acc-001", request))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("do not match");
    }

    // ── Admin operations ───────────────────────────────────────────────────────

    @Test
    @DisplayName("getAllAccounts: returns all accounts as response DTOs")
    void getAllAccounts_returnsAllAccounts() {
        given(accountRepository.findAll()).willReturn(List.of(testAccount));

        List<AccountResponse> result = accountService.getAllAccounts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo(testAccount.getEmail());
    }

    @Test
    @DisplayName("changeStatus: updates account status correctly")
    void changeStatus_updatesStatus() {
        given(accountRepository.findById("acc-001")).willReturn(Optional.of(testAccount));
        given(accountRepository.save(any(Account.class))).willAnswer(inv -> inv.getArgument(0));

        ChangeStatusRequest request = new ChangeStatusRequest(AccountStatus.SUSPENDED);
        AccountResponse response = accountService.changeStatus("acc-001", request);

        assertThat(response.getStatus()).isEqualTo(AccountStatus.SUSPENDED);
    }

    @Test
    @DisplayName("changeRole: updates account role correctly")
    void changeRole_updatesRole() {
        given(accountRepository.findById("acc-001")).willReturn(Optional.of(testAccount));
        given(accountRepository.save(any(Account.class))).willAnswer(inv -> inv.getArgument(0));

        ChangeRoleRequest request = new ChangeRoleRequest(Role.DRIVER);
        AccountResponse response = accountService.changeRole("acc-001", request);

        assertThat(response.getRole()).isEqualTo(Role.DRIVER);
    }

    @Test
    @DisplayName("deleteAccount: deletes account when found")
    void deleteAccount_whenFound_deletes() {
        given(accountRepository.findById("acc-001")).willReturn(Optional.of(testAccount));
        willDoNothing().given(accountRepository).delete(testAccount);

        assertThatNoException().isThrownBy(() -> accountService.deleteAccount("acc-001"));
        then(accountRepository).should().delete(testAccount);
    }
}
