package com.exadel.tenderflex.service.api;

import com.exadel.tenderflex.core.dto.input.UserDtoInput;
import com.exadel.tenderflex.core.dto.input.UserDtoRegistration;
import com.exadel.tenderflex.core.dto.output.UserDtoOutput;
import com.exadel.tenderflex.core.dto.output.UserRegistrationDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.PageDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.UserPageForAdminDtoOutput;
import org.springframework.data.domain.Pageable;

public interface IUserManager extends IManager<UserDtoOutput, UserDtoInput, UserDtoOutput>,
        IManagerUpdate<UserDtoOutput, UserDtoInput> {
    UserRegistrationDtoOutput saveUser(UserDtoRegistration user);

    UserDtoOutput getUserDto(String email);
    PageDtoOutput<UserPageForAdminDtoOutput> getDtoForAdmin(Pageable pageable);
}
