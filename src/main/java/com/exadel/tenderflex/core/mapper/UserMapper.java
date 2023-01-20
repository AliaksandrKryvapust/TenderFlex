package com.exadel.tenderflex.core.mapper;

import com.exadel.tenderflex.core.dto.input.UserDtoInput;
import com.exadel.tenderflex.core.dto.input.UserDtoRegistration;
import com.exadel.tenderflex.core.dto.output.UserDtoOutput;
import com.exadel.tenderflex.core.dto.output.UserLoginDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.PageDtoOutput;
import com.exadel.tenderflex.repository.entity.EUserRole;
import com.exadel.tenderflex.repository.entity.EUserStatus;
import com.exadel.tenderflex.repository.entity.Role;
import com.exadel.tenderflex.repository.entity.User;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserMapper {
    private final PasswordEncoder encoder;

    public UserMapper(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    public User userInputMapping(UserDtoRegistration userDtoRegistration) {
        Role role = Role.builder()
                .role(userDtoRegistration.getRole())
                .build();
        return User.builder().username(userDtoRegistration.getUsername())
                .password(encoder.encode(userDtoRegistration.getPassword()))
                .email(userDtoRegistration.getEmail())
                .roles(Collections.singletonList(role))
                .status(EUserStatus.ACTIVATED)
                .build();
    }

    public User inputMapping(UserDtoInput userDtoInput) {
        Role role = Role.builder()
                .role(EUserRole.valueOf(userDtoInput.getRole()))
                .build();
        return User.builder()
                .username(userDtoInput.getUsername())
                .password(encoder.encode(userDtoInput.getPassword()))
                .email(userDtoInput.getEmail())
                .roles(Collections.singletonList(role))
                .status(EUserStatus.valueOf(userDtoInput.getStatus()))
                .build();
    }

    public UserLoginDtoOutput registerOutputMapping(User user) {
        return UserLoginDtoOutput.builder()
                .email(user.getEmail())
                .build();
    }

    public UserLoginDtoOutput loginOutputMapping(UserDetails userDetails, String token) {
        return UserLoginDtoOutput.builder()
                .email(userDetails.getUsername())
                .token(token)
                .build();
    }

    public UserDtoOutput outputMapping(User user) {
        return UserDtoOutput.builder()
                .id(String.valueOf(user.getId()))
                .dtCreate(user.getDtCreate())
                .dtUpdate(user.getDtUpdate())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRoles().get(0).getRole())
                .status(user.getStatus())
                .build();
    }

    public PageDtoOutput<UserDtoOutput> outputPageMapping(Page<User> record) {
        List<UserDtoOutput> outputs = record.getContent().stream().map(this::outputMapping).collect(Collectors.toList());
        return PageDtoOutput.<UserDtoOutput>builder()
                .number(record.getNumber() + 1)
                .size(record.getSize())
                .totalPages(record.getTotalPages())
                .totalElements(record.getTotalElements())
                .first(record.isFirst())
                .numberOfElements(record.getNumberOfElements())
                .last(record.isLast())
                .content(outputs)
                .build();
    }
}
