package com.exadel.tenderflex.core.dto.input;

import com.exadel.tenderflex.controller.validator.api.IValidEnum;
import com.exadel.tenderflex.repository.entity.ECurrency;
import com.exadel.tenderflex.repository.entity.ETenderStatus;
import com.exadel.tenderflex.repository.entity.ETenderType;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.springframework.lang.Nullable;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Builder
@Data
@Jacksonized
public class TenderDtoInput {
    @Valid
    @NotNull(message = "company details cannot be null")
    private CompanyDetailsDtoInput contractor;
    @Valid
    @NotNull(message = "contact person cannot be null")
    private ContactPersonDtoInput contactPerson;
    @NotNull(message = "cpv Code cannot be null")
    private String cpvCode;
    @NotNull(message = "user role cannot be null")
    @IValidEnum(enumClass = ETenderType.class, message = "tender type does not match")
    private String tenderType;
    @Nullable
    @Size(max = 250, message = "description should contain less than 250 letters")
    private String description;
    @NotNull(message = "minimal price cannot be null")
    @Positive(message = "minimal price cannot be negative")
    private Integer minPrice;
    @NotNull(message = "maximal price cannot be null")
    @Positive(message = "maximal price cannot be negative")
    private Integer maxPrice;
    @NotNull(message = "currency cannot be null")
    @IValidEnum(enumClass = ECurrency.class, message = "currency does not match")
    private String currency;
    @NotNull(message = "publication date cannot be null")
    @Past(message = "publication date should refer to moment in the past")
    private LocalDate publication;
    @NotNull(message = "submission deadline cannot be null")
    @Future(message = "submission deadline should refer to moment in the future")
    private LocalDate submissionDeadline;
    @NotNull(message = "tender status cannot be null")
    @IValidEnum(enumClass = ETenderStatus.class, message = "tender status does not match")
    private ETenderStatus tenderStatus;
}
