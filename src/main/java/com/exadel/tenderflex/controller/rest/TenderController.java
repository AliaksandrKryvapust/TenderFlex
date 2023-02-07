package com.exadel.tenderflex.controller.rest;

import com.exadel.tenderflex.core.dto.input.ActionDto;
import com.exadel.tenderflex.core.dto.output.TenderDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.OfferPageForContractorDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.PageDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.TenderPageForBidderDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.TenderPageForContractorDtoOutput;
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

import javax.validation.Valid;
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
    public ResponseEntity<PageDtoOutput<TenderPageForContractorDtoOutput>> getPage(@RequestParam("page") int page,
                                                                                      @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dtCreate").descending());
        PageDtoOutput<TenderPageForContractorDtoOutput> dtoOutput = tenderManager.getDto(pageable);
        return ResponseEntity.ok(dtoOutput);
    }

    @GetMapping(path = "/all",params = {"page", "size"})
    public ResponseEntity<PageDtoOutput<TenderPageForBidderDtoOutput>> getPageAll(@RequestParam("page") int page,
                                                                                  @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dtCreate").ascending());
        PageDtoOutput<TenderPageForBidderDtoOutput> dtoOutput = tenderManager.getDtoAll(pageable);
        return ResponseEntity.ok(dtoOutput);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TenderDtoOutput> get(@PathVariable UUID id) {
        TenderDtoOutput dtoOutput = tenderManager.getDto(id);
        return ResponseEntity.ok(dtoOutput);
    }

    @GetMapping(path = "/{id}/offer", params = {"page", "size"})
    public ResponseEntity<PageDtoOutput<OfferPageForContractorDtoOutput>> getPageForTender(@PathVariable UUID id,
                                                                                           @RequestParam("page") int page,
                                                                                           @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dtCreate").descending());
        PageDtoOutput<OfferPageForContractorDtoOutput> dtoOutput = tenderManager.getOfferForTender(id, pageable);
        return ResponseEntity.ok(dtoOutput);
    }

    @GetMapping(path = "/offer", params = {"page", "size"})
    public ResponseEntity<PageDtoOutput<OfferPageForContractorDtoOutput>> getPageForContractor(@RequestParam("page") int page,
                                                                                               @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dtCreate").descending());
        PageDtoOutput<OfferPageForContractorDtoOutput> dtoOutput = tenderManager.getOfferForContractor(pageable);
        return ResponseEntity.ok(dtoOutput);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<TenderDtoOutput> postWithFile(@RequestParam(value = "tender") String tender,
                                                        @RequestParam(value = "contract", required = false) MultipartFile contract,
                                                        @RequestParam(value = "award", required = false) MultipartFile award,
                                                        @RequestParam(value = "reject", required = false) MultipartFile reject) {
        Map<EFileType, MultipartFile> files = collectFiles(contract, award, reject);
        TenderDtoOutput dtoOutput = tenderManager.saveDto(tender, files);
        return new ResponseEntity<>(dtoOutput, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{id}/version/{version}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<TenderDtoOutput> put(@PathVariable UUID id, @PathVariable(name = "version") String version,
                                               @RequestParam(value = "tender") String tender,
                                               @RequestParam(value = "contract", required = false) MultipartFile contract,
                                               @RequestParam(value = "award", required = false) MultipartFile award,
                                               @RequestParam(value = "reject", required = false) MultipartFile reject) {
        Map<EFileType, MultipartFile> files = collectFiles(contract, award, reject);
        TenderDtoOutput dtoOutput = tenderManager.updateDto(tender, files, id, Long.valueOf(version));
        return ResponseEntity.ok(dtoOutput);
    }

    @PostMapping(path = "/action")
    public ResponseEntity<TenderDtoOutput> postAction(@RequestBody @Valid ActionDto actionDto) {
        TenderDtoOutput dtoOutput = tenderManager.awardAction(actionDto);
        return new ResponseEntity<>(dtoOutput, HttpStatus.CREATED);
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
