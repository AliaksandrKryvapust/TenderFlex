package com.exadel.tenderflex.controller.rest;

import com.exadel.tenderflex.controller.utils.JwtTokenUtil;
import com.exadel.tenderflex.core.dto.output.*;
import com.exadel.tenderflex.core.dto.output.pages.OfferPageForBidderDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.PageDtoOutput;
import com.exadel.tenderflex.repository.entity.enums.ECurrency;
import com.exadel.tenderflex.repository.entity.enums.EFileType;
import com.exadel.tenderflex.repository.entity.enums.EOfferStatus;
import com.exadel.tenderflex.service.JwtUserDetailsService;
import com.exadel.tenderflex.service.api.IOfferManager;
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
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(controllers = OfferController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@AutoConfigureMockMvc
class OfferControllerTest {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private IOfferManager offerManager;
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
    final String contentType = "application/pdf";
    final String fileName = "testFile";
    final String url = "http//localhost:8082";
    final Integer maxPrice = 10800;
    final String dt_create = "12/01/2023";
    final String json = "{\n" +
            "    \"company_details\": {\n" +
            "        \"official_name\": \"SupplyCompany\",\n" +
            "        \"registration_number\": \"ULG NL 0310 777 861\",\n" +
            "        \"country\": \"SPAIN\"\n" +
            "    },\n" +
            "    \"contact_person\": {\n" +
            "        \"name\": \"Luca\",\n" +
            "        \"surname\": \"Brasa\",\n" +
            "        \"phone_number\": 3913302698751\n" +
            "    },\n" +
            "    \"bid_price\": 1062,\n" +
            "    \"currency\": \"EURO\",\n" +
            "    \"tender_id\": \"39380b8c-839a-497f-88a7-740884c1f5f1\"\n" +
            "}";

    @Test
    @WithMockUser(username = "bidder@gmail.com", password = "dff45t", roles = {"BIDDER"})
    void getPage() throws Exception {
        // preconditions
        final PageDtoOutput<OfferPageForBidderDtoOutput> pageDtoOutput = getPreparedPageDtoOutput();
        final Pageable pageable = PageRequest.of(0, 1, Sort.by("dtCreate").descending());
        Mockito.when(offerManager.getDto(pageable)).thenReturn(pageDtoOutput);

        // assert
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/offer").param("page", "0")
                        .param("size", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].user.email").value(email))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].user.token").value(token))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].field_from_tender_cpv_code").value(cpvCode))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].official_name").value(officialName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].bid_price").value(maxPrice))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].country").value(country))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].dt_create").value(dt_create))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[*].offer_status").value(EOfferStatus.OFFER_SENT.name()));

        //test
        Mockito.verify(offerManager).getDto(pageable);
    }

    @Test
    @WithMockUser(username = "bidder@gmail.com", password = "dff45t", roles = {"BIDDER"})
    void get() throws Exception {
        // preconditions
        final OfferDtoOutput dtoOutput = getPreparedOfferDtoOutput();
        Mockito.when(offerManager.getDto(UUID.fromString(id))).thenReturn(dtoOutput);

        // assert
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/offer/" + id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.bidder.official_name").value(officialName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.bidder.registration_number").value(registrationNumber))
                .andExpect(MockMvcResultMatchers.jsonPath("$.bidder.country").value(country))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contact_person.name").value(name))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contact_person.surname").value(surname))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contact_person.phone_number").value(phoneNumber))
                .andExpect(MockMvcResultMatchers.jsonPath("$.bid_price").value(maxPrice))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currency").value(ECurrency.NOK.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dt_create").value(dtCreate.toEpochMilli()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dt_update").value(dtUpdate.toEpochMilli()));

        //test
        Mockito.verify(offerManager).getDto(UUID.fromString(id));
    }

    @Test
    @WithMockUser(username = "bidder@gmail.com", password = "dff45t", roles = {"BIDDER"})
    void postWithFile() throws Exception {
        // preconditions
        final OfferDtoOutput dtoOutput = getPreparedOfferDtoOutput();
        Mockito.when(offerManager.saveDto(any(String.class), any(Map.class))).thenReturn(dtoOutput);

        // assert
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/offer").param("offer", json)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.bidder.official_name").value(officialName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.bidder.registration_number").value(registrationNumber))
                .andExpect(MockMvcResultMatchers.jsonPath("$.bidder.country").value(country))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contact_person.name").value(name))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contact_person.surname").value(surname))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contact_person.phone_number").value(phoneNumber))
                .andExpect(MockMvcResultMatchers.jsonPath("$.bid_price").value(maxPrice))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currency").value(ECurrency.NOK.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dt_create").value(dtCreate.toEpochMilli()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dt_update").value(dtUpdate.toEpochMilli()));

        //test
        Mockito.verify(offerManager).saveDto(any(String.class), any(Map.class));
    }

    @Test
    @WithMockUser(username = "bidder@gmail.com", password = "dff45t", roles = {"BIDDER"})
    void put() throws Exception {
        // preconditions
        final OfferDtoOutput dtoOutput = getPreparedOfferDtoOutput();
        Mockito.when(offerManager.updateDto(any(String.class), any(Map.class), any(UUID.class), any(Long.class))).thenReturn(dtoOutput);

        // assert
        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/offer/" + id + "/version/" + dtUpdate.toEpochMilli())
                        .param("offer", json).contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.bidder.official_name").value(officialName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.bidder.registration_number").value(registrationNumber))
                .andExpect(MockMvcResultMatchers.jsonPath("$.bidder.country").value(country))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contact_person.name").value(name))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contact_person.surname").value(surname))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contact_person.phone_number").value(phoneNumber))
                .andExpect(MockMvcResultMatchers.jsonPath("$.bid_price").value(maxPrice))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currency").value(ECurrency.NOK.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dt_create").value(dtCreate.toEpochMilli()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dt_update").value(dtUpdate.toEpochMilli()));

        //test
        Mockito.verify(offerManager).updateDto(any(String.class), any(Map.class), any(UUID.class), any(Long.class));
    }

    OfferDtoOutput getPreparedOfferDtoOutput() {
        return OfferDtoOutput.builder()
                .id(id)
                .bidder(getPreparedCompanyDetailsDtoOutput())
                .contactPerson(getPreparedContactPersonDtoOutput())
                .propositionFile(getPreparedFileDtoOutput())
                .bidPrice(maxPrice)
                .currency(ECurrency.NOK.name())
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .build();
    }

    PageDtoOutput<OfferPageForBidderDtoOutput> getPreparedPageDtoOutput() {
        return PageDtoOutput.<OfferPageForBidderDtoOutput>builder()
                .number(2)
                .size(1)
                .totalPages(1)
                .totalElements(1L)
                .first(true)
                .numberOfElements(1)
                .last(true)
                .content(Collections.singleton(getPreparedOfferPageDtoOutput()))
                .build();
    }

    OfferPageForBidderDtoOutput getPreparedOfferPageDtoOutput() {
        return OfferPageForBidderDtoOutput.builder()
                .id(id)
                .user(getPreparedUserLoginDtoOutput())
                .fieldFromTenderCpvCode(cpvCode)
                .officialName(officialName)
                .bidPrice(maxPrice)
                .country(country)
                .dtCreate(dtCreate.atZone(ZoneOffset.UTC).toLocalDate())
                .offerStatus(EOfferStatus.OFFER_SENT.name())
                .build();
    }

    UserLoginDtoOutput getPreparedUserLoginDtoOutput() {
        return UserLoginDtoOutput.builder()
                .email(email)
                .token(token).build();
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

    FileDtoOutput getPreparedFileDtoOutput() {
        return FileDtoOutput.builder()
                .id(id)
                .fileType(EFileType.PROPOSITION.name())
                .contentType(contentType)
                .fileName(fileName)
                .url(url)
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate).build();
    }
}