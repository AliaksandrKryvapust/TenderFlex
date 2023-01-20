package com.exadel.tenderflex.controller.rest;

import com.exadel.tenderflex.core.dto.input.UserDtoLogin;
import com.exadel.tenderflex.core.dto.input.UserDtoRegistration;
import com.exadel.tenderflex.core.dto.output.UserDtoOutput;
import com.exadel.tenderflex.core.dto.output.UserLoginDtoOutput;
import com.exadel.tenderflex.service.api.IUserManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Validated
@RequestMapping("/api/v1/users")
public class UserLoginController {
    private final IUserManager userManager;

    public UserLoginController(IUserManager userManager) {
        this.userManager = userManager;
    }

    @GetMapping
    protected ResponseEntity<UserDtoOutput> getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        return ResponseEntity.ok(this.userManager.getUserDto(username));
    }

    @PostMapping("/login")
    protected ResponseEntity<UserLoginDtoOutput> login(@RequestBody @Valid UserDtoLogin dtoLogin) {
        UserLoginDtoOutput userLoginDtoOutput = userManager.login(dtoLogin);
        return ResponseEntity.ok(userLoginDtoOutput);
    }

    @PostMapping("/registration")
    protected ResponseEntity<UserLoginDtoOutput> registration(@RequestBody @Valid UserDtoRegistration dtoInput) {
        UserLoginDtoOutput userLoginDtoOutput = this.userManager.saveUser(dtoInput);
        return new ResponseEntity<>(userLoginDtoOutput, HttpStatus.CREATED);
    }
}
