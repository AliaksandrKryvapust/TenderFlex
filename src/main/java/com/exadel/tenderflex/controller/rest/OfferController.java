package com.exadel.tenderflex.controller.rest;

import com.exadel.tenderflex.core.dto.input.ActionDto;
import com.exadel.tenderflex.core.dto.output.OfferDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.OfferPageForBidderDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.PageDtoOutput;
import com.exadel.tenderflex.repository.entity.enums.EFileType;
import com.exadel.tenderflex.service.api.IOfferManager;
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
@RequestMapping("/api/v1/offer")
@RequiredArgsConstructor
public class OfferController {
    private final IOfferManager offerManager;

    @GetMapping(params = {"page", "size"})
    public ResponseEntity<PageDtoOutput<OfferPageForBidderDtoOutput>> getPage(@RequestParam("page") int page,
                                                                                 @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dtCreate").descending());
        PageDtoOutput<OfferPageForBidderDtoOutput> dtoOutput = offerManager.getDto(pageable);
        return ResponseEntity.ok(dtoOutput);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfferDtoOutput> get(@PathVariable UUID id) {
        OfferDtoOutput dtoOutput = offerManager.getDto(id);
        return ResponseEntity.ok(dtoOutput);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<OfferDtoOutput> postWithFile(@RequestParam(value = "offer") String offer,
                                                          @RequestParam(value = "proposal", required = false) MultipartFile proposal) {
        Map<EFileType, MultipartFile> files = collectFiles(proposal);
        OfferDtoOutput dtoOutput = offerManager.saveDto(offer, files);
        return new ResponseEntity<>(dtoOutput, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{id}/version/{version}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<OfferDtoOutput> put(@PathVariable UUID id, @PathVariable(name = "version") String version,
                                              @RequestParam(value = "offer") String offer,
                                              @RequestParam(value = "proposal", required = false) MultipartFile proposal) {
        Map<EFileType, MultipartFile> files = collectFiles(proposal);
        OfferDtoOutput dtoOutput = offerManager.updateDto(offer, files, id, Long.valueOf(version));
        return ResponseEntity.ok(dtoOutput);
    }

    @PostMapping(path = "/action")
    public ResponseEntity<OfferDtoOutput> postAction(@RequestBody @Valid ActionDto actionDto) {
        OfferDtoOutput dtoOutput = offerManager.awardAction(actionDto);
        return new ResponseEntity<>(dtoOutput, HttpStatus.CREATED);
    }

    @NonNull
    private Map<EFileType, MultipartFile> collectFiles(MultipartFile proposal) {
        Map<EFileType, MultipartFile> files = new HashMap<>();
        files.put(EFileType.PROPOSITION, proposal);
        return files;
    }
}
