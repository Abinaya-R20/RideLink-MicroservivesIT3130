package com.ridelink.accountservice.controller;

import com.ridelink.accountservice.dto.AccountResponse;
import com.ridelink.accountservice.dto.ChangeRoleRequest;
import com.ridelink.accountservice.dto.ChangeStatusRequest;
import com.ridelink.accountservice.model.AccountStatus;
import com.ridelink.accountservice.model.Role;
import com.ridelink.accountservice.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/accounts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin - Accounts", description = "Admin endpoints for managing all accounts")
public class AdminAccountController {

    private final AccountService accountService;

    @GetMapping
    @Operation(summary = "Get all accounts")
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an account by ID")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable String id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @GetMapping("/by-role/{role}")
    @Operation(summary = "Get all accounts with a given role")
    public ResponseEntity<List<AccountResponse>> getByRole(@PathVariable Role role) {
        return ResponseEntity.ok(accountService.getAccountsByRole(role));
    }

    @GetMapping("/by-status/{status}")
    @Operation(summary = "Get all accounts with a given status")
    public ResponseEntity<List<AccountResponse>> getByStatus(@PathVariable AccountStatus status) {
        return ResponseEntity.ok(accountService.getAccountsByStatus(status));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Change the status of an account")
    public ResponseEntity<AccountResponse> changeStatus(
            @PathVariable String id,
            @Valid @RequestBody ChangeStatusRequest request) {

        return ResponseEntity.ok(accountService.changeStatus(id, request));
    }

    @PatchMapping("/{id}/role")
    @Operation(summary = "Change the role of an account")
    public ResponseEntity<AccountResponse> changeRole(
            @PathVariable String id,
            @Valid @RequestBody ChangeRoleRequest request) {

        return ResponseEntity.ok(accountService.changeRole(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an account by ID")
    public ResponseEntity<Void> deleteAccount(@PathVariable String id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
