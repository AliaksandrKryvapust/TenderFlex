package com.exadel.tenderflex.controller.rest;

import com.exadel.tenderflex.core.dto.input.TenderDtoInput;
import com.exadel.tenderflex.core.dto.output.TenderDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.PageDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.TenderPageDtoOutput;
import com.exadel.tenderflex.service.api.ITenderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/api/v1/tender")
@RequiredArgsConstructor
public class TenderController {
    private final ITenderManager tenderManager;

    @GetMapping(params = {"page", "size"})
    protected ResponseEntity<PageDtoOutput<TenderPageDtoOutput>> getPage(@RequestParam("page") int page,
                                                                         @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(tenderManager.getDto(pageable));
    }

    @GetMapping("/{id}")
    protected ResponseEntity<TenderDtoOutput> get(@PathVariable UUID id) {
        return ResponseEntity.ok(tenderManager.getDto(id));
    }

    @PostMapping
    protected ResponseEntity<TenderDtoOutput> post(@RequestBody @Valid TenderDtoInput dtoInput) {
        return new ResponseEntity<>(this.tenderManager.saveDto(dtoInput), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/dt_update/{version}")
    protected ResponseEntity<TenderDtoOutput> put(@PathVariable UUID id, @PathVariable(name = "version") String version,
                                                  @Valid @RequestBody TenderDtoInput dtoInput) {
        return ResponseEntity.ok(this.tenderManager.updateDto(dtoInput, id, Long.valueOf(version)));
    }
}
