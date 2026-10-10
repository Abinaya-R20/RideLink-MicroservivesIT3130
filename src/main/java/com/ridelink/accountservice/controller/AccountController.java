package com.ridelink.accountservice.controller;

import com.ridelink.accountservice.dto.AccountResponse;
import com.ridelink.accountservice.dto.ChangePasswordRequest;
import com.ridelink.accountservice.dto.UpdateProfileRequest;
import com.ridelink.accountservice.model.Account;
import com.ridelink.accountservice.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Account", description = "Endpoints for authenticated users to manage their own account")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/me")
    @Operation(summary = "Get the currently authenticated account")
    public ResponseEntity<AccountResponse> getMyAccount(@AuthenticationPrincipal Account account) {
        return ResponseEntity.ok(accountService.getAccountById(account.getId()));
    }

    @PutMapping("/me")
    @Operation(summary = "Update the currently authenticated account's profile")
    public ResponseEntity<AccountResponse> updateMyProfile(
            @AuthenticationPrincipal Account account,
            @Valid @RequestBody UpdateProfileRequest request) {

        return ResponseEntity.ok(accountService.updateProfile(account.getId(), request));
    }

    @PatchMapping("/me/password")
    @Operation(summary = "Change the currently authenticated account's password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal Account account,
            @Valid @RequestBody ChangePasswordRequest request) {

        accountService.changePassword(account.getId(), request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    @Operation(summary = "Delete the currently authenticated account")
    public ResponseEntity<Void> deleteMyAccount(@AuthenticationPrincipal Account account) {
        accountService.deleteAccount(account.getId());
        return ResponseEntity.noContent().build();
    }
}
