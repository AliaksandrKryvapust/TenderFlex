package com.exadel.tenderflex.controller.rest;

import com.exadel.tenderflex.core.dto.input.UserDtoInput;
import com.exadel.tenderflex.core.dto.output.UserDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.PageDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.UserPageForAdminDtoOutput;
import com.exadel.tenderflex.service.api.IUserManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/api/v1/admin")
public class UserAdminController {
    private final IUserManager userManager;

    public UserAdminController(IUserManager userManager) {
        this.userManager = userManager;
    }

    @GetMapping(params = {"page", "size"})
    public ResponseEntity<PageDtoOutput<UserPageForAdminDtoOutput>> getPage(@RequestParam("page") int page,
                                                                            @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dtCreate").descending());
        PageDtoOutput<UserPageForAdminDtoOutput> dtoOutput = userManager.getDtoForAdmin(pageable);
        return ResponseEntity.ok(dtoOutput);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDtoOutput> get(@PathVariable UUID id) {
        UserDtoOutput dtoOutput = userManager.getDto(id);
        return ResponseEntity.ok(dtoOutput);
    }

    @PostMapping
    public ResponseEntity<UserDtoOutput> post(@RequestBody @Valid UserDtoInput dtoInput) {
        UserDtoOutput dtoOutput = userManager.saveDto(dtoInput);
        return new ResponseEntity<>(dtoOutput, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/version/{version}")
    public ResponseEntity<UserDtoOutput> put(@PathVariable UUID id, @PathVariable(name = "version") String version,
                                                @Valid @RequestBody UserDtoInput dtoInput) {
        UserDtoOutput dtoOutput = userManager.updateDto(dtoInput, id, Long.valueOf(version));
        return ResponseEntity.ok(dtoOutput);
    }
}
