package com.exadel.tenderflex.controller.rest;

import com.exadel.tenderflex.controller.utils.JwtTokenUtil;
import com.exadel.tenderflex.core.dto.input.ActionDto;
import com.exadel.tenderflex.core.dto.output.*;
import com.exadel.tenderflex.core.dto.output.pages.OfferPageForContractorDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.PageDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.TenderPageForBidderDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.TenderPageForContractorDtoOutput;
import com.exadel.tenderflex.repository.entity.enums.*;
import com.exadel.tenderflex.service.JwtUserDetailsService;
import com.exadel.tenderflex.service.api.ITenderManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(controllers = TenderController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@AutoConfigureMockMvc
class TenderControllerTest {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ITenderManager tenderManager;
    @MockBean
    SecurityContext securityContext;
    // Beans for JwtFilter
    @MockBean
    private JwtTokenUtil tokenUtil;
    @MockBean
    private JwtUserDetailsService userDetailsService;
    // preconditions
    final Instant dtCreate = Instant.ofEpochMilli(1673532204657L);
    final Instant dtUpdate = Instant.ofEpochMilli(1673532532870L);
    final String email = "contractor@gmail.com";
    final String id = "58c635ab-8bac-4899-bc30-b0bb4524c28b";
    final String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjb250cmFjdG9yQGdtYWlsLmNvbSIsImlhdCI6MTY3NDU2Nzk1OCwiZXhwIjoxNjc0NTcxNTU4fQ.nSk39xKnJPOmuci9TLzIzZyya-sTIm9IZIgAINNFRCw";
    final String cpvCode = "45262420-1 Structural steel erection work for structures";
    final String officialName = "TenderCompany";
    final String registrationNumber = "ULG BE 0325 777 171";
    final String country = "POLAND";
    final String name = "Marek";
    final String surname = "KOWALSKI";
    final Long phoneNumber = 48251173301L;
    final DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    final LocalDate submissionDeadline = LocalDate.parse("04/04/2023", df);
    final String deadline = "04/04/2023";
    final String actualDtCreate = "12/01/2023";
    final Integer offerAmount = 4;
    final String contentType = "application/pdf";
    final String fileName = "testFile";
    final String url = "http//localhost:8082";
    final String description = "New contract";
    final Integer minPrice = 10500;
    final Integer maxPrice = 10800;
    final String json = "{\n" +
            "    \"contractor\": {\n" +
            "        \"official_name\": \"TenderCompany\",\n" +
            "        \"registration_number\": \"ULG BE 0325 777 171\",\n" +
            "        \"country\": \"POLAND\"\n" +
            "    },\n" +
            "    \"contact_person\": {\n" +
            "        \"name\": \"Marek\",\n" +
            "        \"surname\": \"KOWALSKI\",\n" +
            "        \"phone_number\": 48251173301\n" +
            "    },\n" +
            "    \"cpv_code\": \"45262420-1 Structural steel erection work for structures\",\n" +
            "    \"tender_type\": \"SUPPLY\",\n" +
            "    \"min_price\": 1050,\n" +
            "    \"max_price\": 1080,\n" +
            "    \"currency\": \"EURO\",\n" +
            "    \"publication\": \"04/12/2022\",\n" +
            "    \"submission_deadline\": \"04/04/2023\",\n" +
            "    \"contract_deadline\": \"14/04/2023\"\n" +
            "}";

    @Test
    @WithMockUser(username = "contractor@gmail.com", password = "55ffg89", roles = {"CONTRACTOR"})
    void getPage() throws Exception {
        // preconditions
        final PageDtoOutput<TenderPageForContractorDtoOutput> pageDtoOutput = getPreparedPageDtoOutput();
        final Pageable pageable = PageRequest.of(0, 1, Sort.by("dtCreate").descending());
        Mockito.when(tenderManager.getDto(pageable)).thenReturn(pageDtoOutput);

        // assert
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tender").param("page", "0")
                        .param("size", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].offers_amount").value(offerAmount))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].submission_deadline").value(deadline))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].tender_status").value(ETenderStatus.IN_PROGRESS.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].official_name").value(officialName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].cpv_code").value(cpvCode))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].user.email").value(email))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].user.role").value(EUserRole.CONTRACTOR.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].id").value(id));

        //test
        Mockito.verify(tenderManager).getDto(pageable);
    }

    @Test
    void getPageAll() throws Exception {
        // preconditions
        final PageDtoOutput<TenderPageForBidderDtoOutput> pageDtoOutput = getPreparedBidderPageDtoOutput();
        final Pageable pageable = PageRequest.of(0, 1, Sort.by("dtCreate").ascending());
        Mockito.when(tenderManager.getDtoAll(pageable)).thenReturn(pageDtoOutput);

        // assert
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tender/all").param("page", "0")
                        .param("size", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].offer_status").value(EOfferStatus.OFFER_HAS_NOT_SEND.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].submission_deadline").value(deadline))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].tender_status").value(ETenderStatus.IN_PROGRESS.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].official_name").value(officialName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].cpv_code").value(cpvCode))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].id").value(id));

        //test
        Mockito.verify(tenderManager).getDtoAll(pageable);
    }

    @Test
    void getPageForTender() throws Exception {
        // preconditions
        final PageDtoOutput<OfferPageForContractorDtoOutput> pageDtoOutput = getPreparedOfferPageDtoOutput();
        final Pageable pageable = PageRequest.of(0, 1, Sort.by("dtCreate").descending());
        Mockito.when(tenderManager.getOfferForTender(UUID.fromString(id), pageable)).thenReturn(pageDtoOutput);

        // assert
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tender/" + id + "/offer").param("page", "0")
                        .param("size", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].offer_status").value(EOfferStatus.OFFER_RECEIVED.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].dt_create").value(actualDtCreate))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].country").value(country))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].bid_price").value(maxPrice))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].field_from_tender_cpv_code").value(cpvCode))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].official_name").value(officialName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].tender_id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].id").value(id));

        //test
        Mockito.verify(tenderManager).getOfferForTender(UUID.fromString(id), pageable);
    }

    @Test
    void getPageForContractor() throws Exception {
        // preconditions
        final PageDtoOutput<OfferPageForContractorDtoOutput> pageDtoOutput = getPreparedOfferPageDtoOutput();
        final Pageable pageable = PageRequest.of(0, 1, Sort.by("dtCreate").descending());
        Mockito.when(tenderManager.getOfferForContractor(pageable)).thenReturn(pageDtoOutput);

        // assert
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tender/offer").param("page", "0")
                        .param("size", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].offer_status").value(EOfferStatus.OFFER_RECEIVED.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].dt_create").value(actualDtCreate))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].country").value(country))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].bid_price").value(maxPrice))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].field_from_tender_cpv_code").value(cpvCode))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].official_name").value(officialName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].tender_id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].id").value(id));

        //test
        Mockito.verify(tenderManager).getOfferForContractor(pageable);
    }

    @Test
    @WithMockUser(username = "contractor@gmail.com", password = "55ffg89", roles = {"CONTRACTOR"})
    void get() throws Exception {
        // preconditions
        final TenderDtoOutput dtoOutput = getPreparedTenderDtoOutput();
        Mockito.when(tenderManager.getDto(UUID.fromString(id))).thenReturn(dtoOutput);

        // assert
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tender/" + id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contractor.official_name").value(officialName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contractor.registration_number").value(registrationNumber))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contractor.country").value(country))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contact_person.name").value(name))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contact_person.surname").value(surname))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contact_person.phone_number").value(phoneNumber))
                .andExpect(MockMvcResultMatchers.jsonPath("$.language").value(ELanguage.ENGLISH.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.procedure").value(EProcedure.OPEN_PROCEDURE.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cpv_code").value(cpvCode))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tender_type").value(ETenderType.SUPPLY.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(description))
                .andExpect(MockMvcResultMatchers.jsonPath("$.min_price").value(minPrice))
                .andExpect(MockMvcResultMatchers.jsonPath("$.max_price").value(maxPrice))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currency").value(ECurrency.NOK.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.publication").value(deadline))
                .andExpect(MockMvcResultMatchers.jsonPath("$.submission_deadline").value(deadline))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dt_create").value(dtCreate.toEpochMilli()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dt_update").value(dtUpdate.toEpochMilli()));


        //test
        Mockito.verify(tenderManager).getDto(UUID.fromString(id));
    }

    @Test
    @WithMockUser(username = "contractor@gmail.com", password = "55ffg89", roles = {"CONTRACTOR"})
    void postWithFile() throws Exception {
        // preconditions
        final TenderDtoOutput dtoOutput = getPreparedTenderDtoOutput();
        Mockito.when(tenderManager.saveDto(any(String.class), any(Map.class))).thenReturn(dtoOutput);

        // assert
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tender").param("tender", json)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contractor.official_name").value(officialName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contractor.registration_number").value(registrationNumber))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contractor.country").value(country))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contact_person.name").value(name))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contact_person.surname").value(surname))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contact_person.phone_number").value(phoneNumber))
                .andExpect(MockMvcResultMatchers.jsonPath("$.language").value(ELanguage.ENGLISH.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.procedure").value(EProcedure.OPEN_PROCEDURE.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cpv_code").value(cpvCode))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tender_type").value(ETenderType.SUPPLY.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(description))
                .andExpect(MockMvcResultMatchers.jsonPath("$.min_price").value(minPrice))
                .andExpect(MockMvcResultMatchers.jsonPath("$.max_price").value(maxPrice))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currency").value(ECurrency.NOK.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.publication").value(deadline))
                .andExpect(MockMvcResultMatchers.jsonPath("$.submission_deadline").value(deadline))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dt_create").value(dtCreate.toEpochMilli()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dt_update").value(dtUpdate.toEpochMilli()));

        //test
        Mockito.verify(tenderManager).saveDto(any(String.class), any(Map.class));
    }

    @Test
    @WithMockUser(username = "contractor@gmail.com", password = "55ffg89", roles = {"CONTRACTOR"})
    void postAction() throws Exception {
        // preconditions
        final TenderDtoOutput dtoOutput = getPreparedTenderDtoOutput();
        final ActionDto actionDto = getPreparedActionDto();
        Mockito.when(tenderManager.awardAction(any(ActionDto.class))).thenReturn(dtoOutput);

        // assert
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tender/action")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(actionDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contractor.official_name").value(officialName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contractor.registration_number").value(registrationNumber))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contractor.country").value(country))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contact_person.name").value(name))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contact_person.surname").value(surname))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contact_person.phone_number").value(phoneNumber))
                .andExpect(MockMvcResultMatchers.jsonPath("$.language").value(ELanguage.ENGLISH.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.procedure").value(EProcedure.OPEN_PROCEDURE.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cpv_code").value(cpvCode))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tender_type").value(ETenderType.SUPPLY.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(description))
                .andExpect(MockMvcResultMatchers.jsonPath("$.min_price").value(minPrice))
                .andExpect(MockMvcResultMatchers.jsonPath("$.max_price").value(maxPrice))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currency").value(ECurrency.NOK.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.publication").value(deadline))
                .andExpect(MockMvcResultMatchers.jsonPath("$.submission_deadline").value(deadline))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dt_create").value(dtCreate.toEpochMilli()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dt_update").value(dtUpdate.toEpochMilli()));

        //test
        Mockito.verify(tenderManager).awardAction(any(ActionDto.class));
    }

    @Test
    @WithMockUser(username = "contractor@gmail.com", password = "55ffg89", roles = {"CONTRACTOR"})
    void put() throws Exception {
        // preconditions
        final TenderDtoOutput dtoOutput = getPreparedTenderDtoOutput();
        Mockito.when(tenderManager.updateDto(any(String.class), any(Map.class), any(UUID.class), any(Long.class))).thenReturn(dtoOutput);

        // assert
        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/tender/" + id + "/version/" + dtUpdate.toEpochMilli())
                        .param("tender", json).contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contractor.official_name").value(officialName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contractor.registration_number").value(registrationNumber))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contractor.country").value(country))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contact_person.name").value(name))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contact_person.surname").value(surname))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contact_person.phone_number").value(phoneNumber))
                .andExpect(MockMvcResultMatchers.jsonPath("$.language").value(ELanguage.ENGLISH.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.procedure").value(EProcedure.OPEN_PROCEDURE.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cpv_code").value(cpvCode))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tender_type").value(ETenderType.SUPPLY.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(description))
                .andExpect(MockMvcResultMatchers.jsonPath("$.min_price").value(minPrice))
                .andExpect(MockMvcResultMatchers.jsonPath("$.max_price").value(maxPrice))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currency").value(ECurrency.NOK.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.publication").value(deadline))
                .andExpect(MockMvcResultMatchers.jsonPath("$.submission_deadline").value(deadline))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dt_create").value(dtCreate.toEpochMilli()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dt_update").value(dtUpdate.toEpochMilli()));

        //test
        Mockito.verify(tenderManager).updateDto(any(String.class), any(Map.class), any(UUID.class), any(Long.class));
    }

    TenderDtoOutput getPreparedTenderDtoOutput() {
        return TenderDtoOutput.builder()
                .id(id)
                .contractor(getPreparedCompanyDetailsDtoOutput())
                .contactPerson(getPreparedContactPersonDtoOutput())
                .contract(getPreparedContractDtoOutput())
                .rejectDecision(getPreparedRejectDecision())
                .procedure(EProcedure.OPEN_PROCEDURE.name())
                .language(ELanguage.ENGLISH.name())
                .cpvCode(cpvCode)
                .tenderType(ETenderType.SUPPLY.name())
                .description(description)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .currency(ECurrency.NOK.name())
                .publication(submissionDeadline)
                .submissionDeadline(submissionDeadline)
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate).build();
    }

    PageDtoOutput<TenderPageForContractorDtoOutput> getPreparedPageDtoOutput() {
        return PageDtoOutput.<TenderPageForContractorDtoOutput>builder()
                .number(2)
                .size(1)
                .totalPages(1)
                .totalElements(1L)
                .first(true)
                .numberOfElements(1)
                .last(true)
                .content(Collections.singleton(getPreparedTenderPageDtoOutput()))
                .build();
    }

    OfferPageForContractorDtoOutput getPreparedOfferDtoOutput() {
        return OfferPageForContractorDtoOutput.builder()
                .id(id)
                .tenderId(id)
                .officialName(officialName)
                .fieldFromTenderCpvCode(cpvCode)
                .bidPrice(maxPrice)
                .country(country)
                .dtCreate(dtCreate.atZone(ZoneOffset.UTC).toLocalDate())
                .offerStatus(EOfferStatus.OFFER_RECEIVED.name())
                .build();
    }

    PageDtoOutput<OfferPageForContractorDtoOutput> getPreparedOfferPageDtoOutput() {
        return PageDtoOutput.<OfferPageForContractorDtoOutput>builder()
                .number(2)
                .size(1)
                .totalPages(1)
                .totalElements(1L)
                .first(true)
                .numberOfElements(1)
                .last(true)
                .content(Collections.singleton(getPreparedOfferDtoOutput()))
                .build();
    }

    TenderPageForContractorDtoOutput getPreparedTenderPageDtoOutput() {
        return TenderPageForContractorDtoOutput.builder()
                .id(id)
                .user(getPreparedUserLoginDtoOutput())
                .cpvCode(cpvCode)
                .officialName(officialName)
                .tenderStatus(ETenderStatus.IN_PROGRESS.name())
                .submissionDeadline(submissionDeadline)
                .offersAmount(offerAmount).build();
    }

    UserRegistrationDtoOutput getPreparedUserLoginDtoOutput() {
        return UserRegistrationDtoOutput.builder()
                .email(email)
                .role(EUserRole.CONTRACTOR.name())
                .build();
    }

    CompanyDetailsDtoOutput getPreparedCompanyDetailsDtoOutput() {
        return CompanyDetailsDtoOutput.builder()
                .officialName(officialName)
                .registrationNumber(registrationNumber)
                .country(country)
                .build();
    }

    ContactPersonDtoOutput getPreparedContactPersonDtoOutput() {
        return ContactPersonDtoOutput.builder()
                .name(name)
                .surname(surname)
                .phoneNumber(phoneNumber)
                .build();
    }

    ContractDtoOutput getPreparedContractDtoOutput() {
        return ContractDtoOutput.builder()
                .id(id)
                .contractDeadline(submissionDeadline)
                .files(Collections.singleton(getPreparedFileDtoOutput()))
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate).build();
    }

    RejectDecisionDtoOutput getPreparedRejectDecision() {
        return RejectDecisionDtoOutput.builder()
                .id(id)
                .rejectDecision(getPreparedFileDtoOutput())
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate).build();
    }

    FileDtoOutput getPreparedFileDtoOutput() {
        return FileDtoOutput.builder()
                .id(id)
                .fileType(EFileType.AWARD_DECISION.name())
                .contentType(contentType)
                .fileName(fileName)
                .url(url)
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate).build();
    }

    ActionDto getPreparedActionDto() {
        return ActionDto.builder()
                .tender(UUID.fromString(id))
                .offer(UUID.fromString(id))
                .award(true)
                .build();
    }

    PageDtoOutput<TenderPageForBidderDtoOutput> getPreparedBidderPageDtoOutput() {
        return PageDtoOutput.<TenderPageForBidderDtoOutput>builder()
                .number(2)
                .size(1)
                .totalPages(1)
                .totalElements(1L)
                .first(true)
                .numberOfElements(1)
                .last(true)
                .content(Collections.singleton(getPreparedTenderPageBidderDtoOutput()))
                .build();
    }

    TenderPageForBidderDtoOutput getPreparedTenderPageBidderDtoOutput() {
        return TenderPageForBidderDtoOutput.builder()
                .id(id.toString())
                .cpvCode(cpvCode)
                .officialName(officialName)
                .tenderStatus(ETenderStatus.IN_PROGRESS.name())
                .submissionDeadline(submissionDeadline)
                .offerStatus(EOfferStatus.OFFER_HAS_NOT_SEND.name())
                .build();
    }
}