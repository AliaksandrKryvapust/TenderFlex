package com.exadel.tenderflex.controller.rest;

import com.exadel.tenderflex.core.dto.input.UserDtoLogin;
import com.exadel.tenderflex.core.dto.input.UserDtoRegistration;
import com.exadel.tenderflex.core.dto.output.UserDtoOutput;
import com.exadel.tenderflex.core.dto.output.UserLoginDtoOutput;
import com.exadel.tenderflex.core.dto.output.UserRegistrationDtoOutput;
import com.exadel.tenderflex.service.JwtUserDetailsService;
import com.exadel.tenderflex.service.api.IUserManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserLoginController {
    private final IUserManager userManager;
    private final JwtUserDetailsService userDetailsService;

    @GetMapping
    public ResponseEntity<UserDtoOutput> getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        UserDtoOutput dtoOutput = userManager.getUserDto(username);
        return ResponseEntity.ok(dtoOutput);
    }

    @GetMapping("/logout")
    public ResponseEntity<Object> logout(HttpServletRequest request) {
        userDetailsService.logout(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginDtoOutput> login(@RequestBody @Valid UserDtoLogin dtoLogin) {
        UserLoginDtoOutput userLoginDtoOutput = userDetailsService.login(dtoLogin);
        HttpHeaders jwtCookie = createJwtCookie(userLoginDtoOutput.getToken());
        return ResponseEntity.ok().headers(jwtCookie).body(userLoginDtoOutput);
    }

    @PostMapping("/registration")
    public ResponseEntity<UserRegistrationDtoOutput> registration(@RequestBody @Valid UserDtoRegistration dtoInput) {
        UserRegistrationDtoOutput userRegistrationDtoOutput = userManager.saveUser(dtoInput);
        return new ResponseEntity<>(userRegistrationDtoOutput, HttpStatus.CREATED);
    }

    private HttpHeaders createJwtCookie(String token) {
        HttpHeaders responseHeaders = new HttpHeaders();
        HttpCookie responseCookie = userDetailsService.createJwtCookie(token);
        responseHeaders.add(HttpHeaders.SET_COOKIE, responseCookie.toString());
        return responseHeaders;
    }
}
