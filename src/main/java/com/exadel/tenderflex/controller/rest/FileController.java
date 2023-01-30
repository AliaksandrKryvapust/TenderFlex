package com.exadel.tenderflex.controller.rest;

import com.exadel.tenderflex.core.dto.input.FileDtoInput;
import com.exadel.tenderflex.core.dto.output.FileDtoOutput;
import com.exadel.tenderflex.service.api.IFileManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
public class FileController {
    private final IFileManager fileManager;

    @PutMapping("/{id}/version/{version}")
    protected ResponseEntity<FileDtoOutput> put(@PathVariable UUID id, @PathVariable(name = "version") String version,
                                                @Valid @RequestBody FileDtoInput dtoInput) {
        return ResponseEntity.ok(this.fileManager.updateDto(dtoInput, id, Long.valueOf(version)));
    }
}