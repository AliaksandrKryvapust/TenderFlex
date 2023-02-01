package com.exadel.tenderflex.controller.rest;

import com.exadel.tenderflex.core.dto.output.TenderDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.PageDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.TenderPageDtoOutput;
import com.exadel.tenderflex.repository.entity.enums.EFileType;
import com.exadel.tenderflex.service.api.ITenderManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
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
        Pageable pageable = PageRequest.of(page, size, Sort.by("dtCreate").descending());
        return ResponseEntity.ok(tenderManager.getDto(pageable));
    }

    @GetMapping("/{id}")
    protected ResponseEntity<TenderDtoOutput> get(@PathVariable UUID id) {
        return ResponseEntity.ok(tenderManager.getDto(id));
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    protected ResponseEntity<TenderDtoOutput> postWithFile(@RequestParam(value = "tender") String tender,
                                                           @RequestParam(value = "contract", required = false) MultipartFile contract,
                                                           @RequestParam(value = "award", required = false) MultipartFile award,
                                                           @RequestParam(value = "reject", required = false) MultipartFile reject) {
        Map<EFileType, MultipartFile> files = collectFiles(contract, award, reject);
        return new ResponseEntity<>(this.tenderManager.saveDto(tender, files), HttpStatus.CREATED);
    }

    @PutMapping(path = "/{id}/version/{version}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    protected ResponseEntity<TenderDtoOutput> put(@PathVariable UUID id, @PathVariable(name = "version") String version,
                                                  @RequestParam(value = "tender") String tender,
                                                  @RequestParam(value = "contract", required = false) MultipartFile contract,
                                                  @RequestParam(value = "award", required = false) MultipartFile award,
                                                  @RequestParam(value = "reject", required = false) MultipartFile reject) {
        Map<EFileType, MultipartFile> files = collectFiles(contract, award, reject);
        return ResponseEntity.ok(this.tenderManager.updateDto(tender, files, id, Long.valueOf(version)));
    }

    @NonNull
    private Map<EFileType, MultipartFile> collectFiles(MultipartFile contract, MultipartFile award, MultipartFile reject) {
        Map<EFileType, MultipartFile> files = new HashMap<>();
        files.put(EFileType.CONTRACT, contract);
        files.put(EFileType.AWARD_DECISION, award);
        files.put(EFileType.REJECT_DECISION, reject);
        return files;
    }
}
