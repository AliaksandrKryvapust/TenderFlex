package com.exadel.tenderflex.core.dto.input;

import com.exadel.tenderflex.controller.validator.api.IValidEnum;
import com.exadel.tenderflex.repository.entity.enums.ECurrency;
import com.exadel.tenderflex.repository.entity.enums.ETenderType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
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
    private final CompanyDetailsDtoInput contractor;
    @Valid
    @NotNull(message = "contact person cannot be null")
    private final ContactPersonDtoInput contactPerson;
    @NotNull(message = "cpv Code cannot be null")
    private final String cpvCode;
    @NotNull(message = "tender type cannot be null")
    @IValidEnum(enumClass = ETenderType.class, message = "tender type does not match")
    private final String tenderType;
    @Nullable
    @Size(max = 250, message = "description should contain less than 250 letters")
    private final String description;
    @NotNull(message = "minimal price cannot be null")
    @Positive(message = "minimal price cannot be negative")
    private final Integer minPrice;
    @NotNull(message = "maximal price cannot be null")
    @Positive(message = "maximal price cannot be negative")
    private final Integer maxPrice;
    @NotNull(message = "currency cannot be null")
    @IValidEnum(enumClass = ECurrency.class, message = "currency does not match")
    private final String currency;
    @NotNull(message = "publication date cannot be null")
    @Past(message = "publication date should refer to moment in the past")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private final LocalDate publication;
    @NotNull(message = "submission deadline cannot be null")
    @Future(message = "submission deadline should refer to moment in the future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private final LocalDate submissionDeadline;
    @NotNull(message = "contract deadline cannot be null")
    @Future(message = "contract deadline should refer to moment in the future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private final LocalDate contractDeadline;
}
