package com.exadel.tenderflex.service;

import com.exadel.tenderflex.core.dto.input.CompanyDetailsDtoInput;
import com.exadel.tenderflex.core.dto.input.ContactPersonDtoInput;
import com.exadel.tenderflex.core.dto.input.TenderDtoInput;
import com.exadel.tenderflex.core.dto.output.*;
import com.exadel.tenderflex.core.dto.output.pages.PageDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.TenderPageForContractorDtoOutput;
import com.exadel.tenderflex.core.mapper.TenderMapper;
import com.exadel.tenderflex.repository.api.ITenderRepository;
import com.exadel.tenderflex.repository.entity.*;
import com.exadel.tenderflex.repository.entity.enums.*;
import com.exadel.tenderflex.service.api.IAwsS3Service;
import com.exadel.tenderflex.service.transactional.api.ITenderTransactionalService;
import com.exadel.tenderflex.service.validator.api.ITenderValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class TenderServiceTest {
    @InjectMocks
    private TenderService tenderService;
    @Mock
    private ITenderRepository tenderRepository;
    @Mock
    private ITenderValidator tenderValidator;
    @Mock
    private TenderMapper tenderMapper;
    @Mock
    private UserService userService;
    @Mock
    private ITenderTransactionalService tenderTransactionalService;
    @Mock
    private IAwsS3Service awsS3Service;

    // preconditions
    final String username = "someone";
    final String password = "kdrL556D";
    final Instant dtCreate = Instant.ofEpochMilli(1673532204657L);
    final Instant dtUpdate = Instant.ofEpochMilli(1673532532870L);
    final String email = "contractor@gmail.com";
    final UUID id = UUID.fromString("58c635ab-8bac-4899-bc30-b0bb4524c28b");
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
    void save() {
        // preconditions
        final Tender tenderInput = getPreparedTenderInput();
        final Tender tenderOutput = getPreparedTenderOutput();
        Mockito.when(tenderTransactionalService.saveTransactional(tenderInput)).thenReturn(tenderOutput);

        //test
        Tender actual = tenderService.save(tenderInput);

        // assert
        assertNotNull(actual);
        checkTenderOutputFields(actual);
    }

    @Test
    void get() {
        // preconditions
        final Tender tenderOutput = getPreparedTenderOutput();
        final Pageable pageable = Pageable.ofSize(1).first();
        final Page<Tender> page = new PageImpl<>(Collections.singletonList(tenderOutput), pageable, 1);
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(getPreparedUserDetails());
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(tenderRepository.findAllForUser(email, pageable)).thenReturn(page);

        //test
        Page<Tender> actual = tenderService.get(pageable);

        // assert
        assertNotNull(actual);
        assertEquals(1, actual.getTotalPages());
        Assertions.assertTrue(actual.isFirst());
        for (Tender tender : actual.getContent()) {
            checkTenderOutputFields(tender);
        }
    }

    @Test
    void testGet() {
        // preconditions
        final Tender tenderOutput = getPreparedTenderOutput();
        Mockito.when(tenderRepository.findById(id)).thenReturn(Optional.of(tenderOutput));

        //test
        Tender actual = tenderService.get(id);

        // assert
        assertNotNull(actual);
        checkTenderOutputFields(actual);
    }

    @Test
    void update() {
        // preconditions
        final Tender tenderOutput = getPreparedTenderOutput();
        Mockito.when(tenderRepository.findById(id)).thenReturn(Optional.of(tenderOutput));
        Mockito.when(tenderTransactionalService.saveTransactional(tenderOutput)).thenReturn(tenderOutput);
        ArgumentCaptor<Long> actualVersion = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Tender> actualTender = ArgumentCaptor.forClass(Tender.class);

        //test
        Tender actual = tenderService.update(tenderOutput, id, dtUpdate.toEpochMilli());
        Mockito.verify(tenderValidator, Mockito.times(1)).optimisticLockCheck(actualVersion.capture(),
                actualTender.capture());
        Mockito.verify(tenderMapper, Mockito.times(1)).updateEntityFields(actualTender.capture(),
                actualTender.capture());

        // assert
        assertEquals(dtUpdate.toEpochMilli(), actualVersion.getValue());
        assertEquals(tenderOutput, actualTender.getValue());
        assertNotNull(actual);
        checkTenderOutputFields(actual);
    }

    @Test
    void saveDto() {
        // preconditions
        final TenderDtoInput dtoInput = getPreparedTenderDtoInput();
        final TenderDtoOutput dtoOutput = getPreparedTenderDtoOutput();
        final Tender tenderInput = getPreparedTenderInput();
        final Tender tenderOutput = getPreparedTenderOutput();
        Mockito.when(tenderMapper.extractJson(json)).thenReturn(dtoInput);
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(getPreparedUserDetails());
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(userService.getUser(email)).thenReturn(getPreparedUserOutput());
        Mockito.when(tenderMapper.inputMapping(any(TenderDtoInput.class), any(User.class), any(Map.class), any(Map.class)))
                .thenReturn(tenderInput);
        Mockito.when(tenderTransactionalService.saveTransactional(tenderInput)).thenReturn(tenderOutput);
        Mockito.when(tenderMapper.outputMapping(tenderOutput)).thenReturn(dtoOutput);
        ArgumentCaptor<Tender> actualTender = ArgumentCaptor.forClass(Tender.class);

        //test
        TenderDtoOutput actual = tenderService.saveDto(json, new HashMap<>());
        Mockito.verify(tenderValidator, Mockito.times(1)).validateEntity(actualTender.capture());
        Mockito.verify(awsS3Service, Mockito.times(1)).generateUrls(any(Map.class));

        // assert
        assertEquals(tenderInput, actualTender.getValue());
        assertNotNull(actual);
        checkTenderDtoOutputFields(actual);
    }

    @Test
    void getDto() {
        // preconditions
        final Tender tenderOutput = getPreparedTenderOutput();
        final Pageable pageable = Pageable.ofSize(1).first();
        final Page<Tender> page = new PageImpl<>(Collections.singletonList(tenderOutput), pageable, 1);
        final PageDtoOutput<TenderPageForContractorDtoOutput> pageDtoOutput = getPreparedPageDtoOutput();
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(getPreparedUserDetails());
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(tenderRepository.findAllForUser(email, pageable)).thenReturn(page);
        Mockito.when(tenderMapper.outputPageMapping(page)).thenReturn(pageDtoOutput);

        //test
        PageDtoOutput<TenderPageForContractorDtoOutput> actual = tenderService.getDto(pageable);

        // assert
        assertNotNull(actual);
        checkPageDtoOutputFields(actual);
        for (TenderPageForContractorDtoOutput tender : actual.getContent()) {
            checkTenderPageDtoOutputFields(tender);
        }
    }

    @Test
    void testGetDto() {
        // preconditions
        final Tender tenderOutput = getPreparedTenderOutput();
        final TenderDtoOutput tenderDtoOutput = getPreparedTenderDtoOutput();
        Mockito.when(tenderRepository.findById(id)).thenReturn(Optional.of(tenderOutput));
        Mockito.when(tenderMapper.outputMapping(tenderOutput)).thenReturn(tenderDtoOutput);

        //test
        TenderDtoOutput actual = tenderService.getDto(id);

        // assert
        assertNotNull(actual);
        checkTenderDtoOutputFields(actual);
    }

    @Test
    void updateDto() {
        // preconditions
        final TenderDtoInput dtoInput = getPreparedTenderDtoInput();
        final TenderDtoOutput dtoOutput = getPreparedTenderDtoOutput();
        final Tender tenderInput = getPreparedTenderInput();
        final Tender tenderOutput = getPreparedTenderOutput();
        Mockito.when(tenderMapper.extractJson(json)).thenReturn(dtoInput);
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(getPreparedUserDetails());
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(userService.getUser(email)).thenReturn(getPreparedUserOutput());
        Mockito.when(tenderMapper.inputMapping(any(TenderDtoInput.class), any(User.class), any(Map.class), any(Map.class)))
                .thenReturn(tenderInput);
        Mockito.when(tenderRepository.findById(id)).thenReturn(Optional.of(tenderInput));
        Mockito.when(tenderTransactionalService.saveTransactional(tenderInput)).thenReturn(tenderOutput);
        Mockito.when(tenderMapper.outputMapping(tenderOutput)).thenReturn(dtoOutput);
        ArgumentCaptor<Tender> actualTender = ArgumentCaptor.forClass(Tender.class);
        ArgumentCaptor<Long> actualVersion = ArgumentCaptor.forClass(Long.class);

        //test
        TenderDtoOutput actual = tenderService.updateDto(json, new HashMap<>(), id, dtUpdate.toEpochMilli());
        Mockito.verify(tenderValidator, Mockito.times(1)).validateEntity(actualTender.capture());
        Mockito.verify(awsS3Service, Mockito.times(1)).generateUrls(any(Map.class));
        Mockito.verify(tenderValidator, Mockito.times(1)).optimisticLockCheck(actualVersion.capture(),
                actualTender.capture());
        Mockito.verify(tenderMapper, Mockito.times(1)).updateEntityFields(actualTender.capture(),
                actualTender.capture());

        // assert
        assertEquals(tenderInput, actualTender.getValue());
        assertNotNull(actual);
        checkTenderDtoOutputFields(actual);
    }

    Tender getPreparedTenderInput() {
        return Tender.builder()
                .user(getPreparedUserOutput())
                .companyDetails(getPreparedCompanyDetails())
                .contactPerson(getPreparedContactPerson())
                .contract(getPreparedContractOutput())
                .rejectDecision(getPreparedRejectDecisionOutput())
                .cpvCode(cpvCode)
                .tenderType(ETenderType.SUPPLY)
                .description(description)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .currency(ECurrency.NOK)
                .tenderStatus(ETenderStatus.IN_PROGRESS)
                .publication(submissionDeadline)
                .submissionDeadline(submissionDeadline)
                .build();
    }

    Tender getPreparedTenderOutput() {
        Set<Offer> offers = new HashSet<>();
        offers.add(getPreparedOfferOutput());
        return Tender.builder()
                .id(id)
                .user(getPreparedUserOutput())
                .companyDetails(getPreparedCompanyDetails())
                .contactPerson(getPreparedContactPerson())
                .contract(getPreparedContractOutput())
                .rejectDecision(getPreparedRejectDecisionOutput())
                .offers(offers)
                .cpvCode(cpvCode)
                .tenderType(ETenderType.SUPPLY)
                .description(description)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .currency(ECurrency.NOK)
                .tenderStatus(ETenderStatus.IN_PROGRESS)
                .publication(submissionDeadline)
                .submissionDeadline(submissionDeadline)
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .build();
    }

    User getPreparedUserOutput() {
        final Privilege privilege = Privilege.builder()
                .id(id)
                .privilege(ERolePrivilege.CAN_READ_OFFER)
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .build();
        final Role role = Role.builder()
                .id(id)
                .roleType(EUserRole.CONTRACTOR)
                .privileges(new HashSet<>(Collections.singleton(privilege)))
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .build();
        return User.builder()
                .id(id)
                .email(email)
                .password(password)
                .username(username)
                .roles(new HashSet<>(Collections.singleton(role)))
                .status(EUserStatus.ACTIVATED)
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .build();
    }

    CompanyDetails getPreparedCompanyDetails() {
        return CompanyDetails.builder()
                .officialName(officialName)
                .registrationNumber(registrationNumber)
                .country(ECountry.valueOf(country))
                .build();
    }

    ContactPerson getPreparedContactPerson() {
        return ContactPerson.builder()
                .name(name)
                .surname(surname)
                .phoneNumber(phoneNumber)
                .build();
    }

    File getPreparedFileOutput() {
        return File.builder()
                .id(id)
                .fileType(EFileType.AWARD_DECISION)
                .contentType(contentType)
                .fileName(fileName)
                .fileKey(token)
                .url(url)
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .build();
    }

    Contract getPreparedContractOutput() {
        return Contract.builder()
                .id(id)
                .contractDeadline(submissionDeadline)
                .files(Collections.singleton(getPreparedFileOutput()))
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .build();
    }

    RejectDecision getPreparedRejectDecisionOutput() {
        return RejectDecision.builder()
                .id(id)
                .file(getPreparedFileOutput())
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .build();
    }

    TenderDtoInput getPreparedTenderDtoInput() {
        return TenderDtoInput.builder()
                .contractor(getPreparedCompanyDetailsDtoInput())
                .contactPerson(getPreparedContactPersonDtoInput())
                .contractDeadline(submissionDeadline)
                .cpvCode(cpvCode)
                .tenderType(ETenderType.SUPPLY.name())
                .description(description)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .currency(ECurrency.NOK.name())
                .publication(submissionDeadline)
                .submissionDeadline(submissionDeadline)
                .build();
    }

    TenderDtoOutput getPreparedTenderDtoOutput() {
        return TenderDtoOutput.builder()
                .id(id.toString())
                .user(getPreparedUserLoginDtoOutput())
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
                .tenderStatus(ETenderStatus.IN_PROGRESS.name())
                .publication(submissionDeadline)
                .submissionDeadline(submissionDeadline)
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .build();
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

    TenderPageForContractorDtoOutput getPreparedTenderPageDtoOutput() {
        return TenderPageForContractorDtoOutput.builder()
                .id(id.toString())
                .user(getPreparedUserLoginDtoOutput())
                .cpvCode(cpvCode)
                .officialName(officialName)
                .tenderStatus(ETenderStatus.IN_PROGRESS.name())
                .submissionDeadline(submissionDeadline)
                .offersAmount(offerAmount)
                .build();
    }

    UserLoginDtoOutput getPreparedUserLoginDtoOutput() {
        return UserLoginDtoOutput.builder()
                .email(email)
                .token(token)
                .build();
    }

    CompanyDetailsDtoInput getPreparedCompanyDetailsDtoInput() {
        return CompanyDetailsDtoInput.builder()
                .officialName(officialName)
                .registrationNumber(registrationNumber)
                .country(country)
                .build();
    }

    CompanyDetailsDtoOutput getPreparedCompanyDetailsDtoOutput() {
        return CompanyDetailsDtoOutput.builder()
                .officialName(officialName)
                .registrationNumber(registrationNumber)
                .country(country)
                .build();
    }

    ContactPersonDtoInput getPreparedContactPersonDtoInput() {
        return ContactPersonDtoInput.builder()
                .name(name)
                .surname(surname)
                .phoneNumber(phoneNumber)
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
                .id(id.toString())
                .contractDeadline(submissionDeadline)
                .files(Collections.singleton(getPreparedFileDtoOutput()))
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .build();
    }

    RejectDecisionDtoOutput getPreparedRejectDecision() {
        return RejectDecisionDtoOutput.builder()
                .id(id.toString())
                .rejectDecision(getPreparedFileDtoOutput())
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .build();
    }

    FileDtoOutput getPreparedFileDtoOutput() {
        return FileDtoOutput.builder()
                .id(id.toString())
                .fileType(EFileType.AWARD_DECISION.name())
                .contentType(contentType)
                .fileName(fileName)
                .url(url)
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .build();
    }

    UserDetails getPreparedUserDetails(){
        User user = getPreparedUserOutput();
        Set<GrantedAuthority> authorityList = new HashSet<>();
        user.getRoles().forEach((i) -> {
            authorityList.add(new SimpleGrantedAuthority("ROLE_" + i.getRoleType().name()));
            for (Privilege privilege : i.getPrivileges()) {
                authorityList.add(new SimpleGrantedAuthority(privilege.getPrivilege().name()));
            }
        });
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), true,
                true, true, true, authorityList);
    }


    private void checkTenderOutputFields(Tender actual) {
        assertNotNull(actual.getCompanyDetails());
        assertNotNull(actual.getContactPerson());
        assertNotNull(actual.getContract());
        assertNotNull(actual.getRejectDecision());
        assertEquals(id, actual.getId());
        assertEquals(cpvCode, actual.getCpvCode());
        assertEquals(ETenderType.SUPPLY, actual.getTenderType());
        assertEquals(description, actual.getDescription());
        assertEquals(minPrice, actual.getMinPrice());
        assertEquals(maxPrice, actual.getMaxPrice());
        assertEquals(ECurrency.NOK, actual.getCurrency());
        assertEquals(ETenderStatus.IN_PROGRESS, actual.getTenderStatus());
        assertEquals(submissionDeadline, actual.getPublication());
        assertEquals(submissionDeadline, actual.getSubmissionDeadline());
        assertEquals(dtCreate, actual.getDtCreate());
        assertEquals(dtUpdate, actual.getDtUpdate());
        assertEquals(officialName, actual.getCompanyDetails().getOfficialName());
        assertEquals(registrationNumber, actual.getCompanyDetails().getRegistrationNumber());
        assertEquals(ECountry.POLAND, actual.getCompanyDetails().getCountry());
        assertEquals(name, actual.getContactPerson().getName());
        assertEquals(surname, actual.getContactPerson().getSurname());
        assertEquals(phoneNumber, actual.getContactPerson().getPhoneNumber());
        assertEquals(id, actual.getContract().getId());
        assertEquals(submissionDeadline, actual.getContract().getContractDeadline());
        assertEquals(dtCreate, actual.getContract().getDtCreate());
        assertEquals(dtUpdate, actual.getContract().getDtUpdate());
        for (File file : actual.getContract().getFiles()) {
            assertEquals(id, file.getId());
            assertEquals(fileName, file.getFileName());
            assertEquals(token, file.getFileKey());
            assertEquals(contentType, file.getContentType());
            assertEquals(url, file.getUrl());
            assertEquals(EFileType.AWARD_DECISION, file.getFileType());
            assertEquals(dtCreate, file.getDtCreate());
            assertEquals(dtUpdate, file.getDtUpdate());
        }
        assertEquals(id, actual.getRejectDecision().getId());
        assertEquals(dtCreate, actual.getRejectDecision().getDtCreate());
        assertEquals(dtUpdate, actual.getRejectDecision().getDtUpdate());
        assertEquals(id, actual.getRejectDecision().getFile().getId());
        assertEquals(fileName, actual.getRejectDecision().getFile().getFileName());
        assertEquals(token, actual.getRejectDecision().getFile().getFileKey());
        assertEquals(contentType, actual.getRejectDecision().getFile().getContentType());
        assertEquals(url, actual.getRejectDecision().getFile().getUrl());
        assertEquals(EFileType.AWARD_DECISION, actual.getRejectDecision().getFile().getFileType());
        assertEquals(dtCreate, actual.getRejectDecision().getFile().getDtCreate());
        assertEquals(dtUpdate, actual.getRejectDecision().getFile().getDtUpdate());
    }

    private void checkTenderDtoOutputFields(TenderDtoOutput actual){
        assertNotNull(actual.getContractor());
        assertNotNull(actual.getContactPerson());
        assertNotNull(actual.getContract());
        assertNotNull(actual.getRejectDecision());
        assertEquals(id.toString(), actual.getId());
        assertEquals(cpvCode, actual.getCpvCode());
        assertEquals(ETenderType.SUPPLY.name(), actual.getTenderType());
        assertEquals(description, actual.getDescription());
        assertEquals(minPrice, actual.getMinPrice());
        assertEquals(maxPrice, actual.getMaxPrice());
        assertEquals(ECurrency.NOK.name(), actual.getCurrency());
        assertEquals(ETenderStatus.IN_PROGRESS.name(), actual.getTenderStatus());
        assertEquals(submissionDeadline, actual.getPublication());
        assertEquals(submissionDeadline, actual.getSubmissionDeadline());
        assertEquals(dtCreate, actual.getDtCreate());
        assertEquals(dtUpdate, actual.getDtUpdate());
        assertEquals(officialName, actual.getContractor().getOfficialName());
        assertEquals(registrationNumber, actual.getContractor().getRegistrationNumber());
        assertEquals(ECountry.POLAND.name(), actual.getContractor().getCountry());
        assertEquals(name, actual.getContactPerson().getName());
        assertEquals(surname, actual.getContactPerson().getSurname());
        assertEquals(phoneNumber, actual.getContactPerson().getPhoneNumber());
        assertEquals(id.toString(), actual.getContract().getId());
        assertEquals(submissionDeadline, actual.getContract().getContractDeadline());
        assertEquals(dtCreate, actual.getContract().getDtCreate());
        assertEquals(dtUpdate, actual.getContract().getDtUpdate());
        for (FileDtoOutput file : actual.getContract().getFiles()) {
            assertEquals(id.toString(), file.getId());
            assertEquals(fileName, file.getFileName());
            assertEquals(contentType, file.getContentType());
            assertEquals(url, file.getUrl());
            assertEquals(EFileType.AWARD_DECISION.name(), file.getFileType());
            assertEquals(dtCreate, file.getDtCreate());
            assertEquals(dtUpdate, file.getDtUpdate());
        }
        assertEquals(id.toString(), actual.getRejectDecision().getId());
        assertEquals(dtCreate, actual.getRejectDecision().getDtCreate());
        assertEquals(dtUpdate, actual.getRejectDecision().getDtUpdate());
        assertEquals(id.toString(), actual.getRejectDecision().getRejectDecision().getId());
        assertEquals(fileName, actual.getRejectDecision().getRejectDecision().getFileName());
        assertEquals(contentType, actual.getRejectDecision().getRejectDecision().getContentType());
        assertEquals(url, actual.getRejectDecision().getRejectDecision().getUrl());
        assertEquals(EFileType.AWARD_DECISION.name(), actual.getRejectDecision().getRejectDecision().getFileType());
        assertEquals(dtCreate, actual.getRejectDecision().getRejectDecision().getDtCreate());
        assertEquals(dtUpdate, actual.getRejectDecision().getRejectDecision().getDtUpdate());
    }

    Offer getPreparedOfferOutput() {
        return Offer.builder()
                .id(id)
                .user(getPreparedUserOutput())
                .bidder(getPreparedCompanyDetails())
                .contactPerson(getPreparedContactPerson())
                .propositionFile(getPreparedFileOutput())
                .tenderId(id)
                .bidPrice(maxPrice)
                .currency(ECurrency.NOK)
                .offerStatusBidder(EOfferStatus.OFFER_SENT)
                .offerStatusContractor(EOfferStatus.OFFER_RECEIVED)
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .build();
    }

    private void checkTenderPageDtoOutputFields(TenderPageForContractorDtoOutput actual){
        assertEquals(id.toString(), actual.getId());
        assertEquals(cpvCode, actual.getCpvCode());
        assertEquals(ETenderStatus.IN_PROGRESS.name(), actual.getTenderStatus());
        assertEquals(submissionDeadline, actual.getSubmissionDeadline());
        assertEquals(offerAmount, actual.getOffersAmount());
        assertEquals(officialName, actual.getOfficialName());
        assertEquals(email, actual.getUser().getEmail());
        assertEquals(token, actual.getUser().getToken());
    }

    private void checkPageDtoOutputFields(PageDtoOutput<TenderPageForContractorDtoOutput> actual) {
        assertEquals(1, actual.getTotalPages());
        Assertions.assertTrue(actual.getFirst());
        Assertions.assertTrue(actual.getLast());
        assertEquals(2, actual.getNumber());
        assertEquals(1, actual.getNumberOfElements());
        assertEquals(1, actual.getSize());
        assertEquals(1, actual.getTotalPages());
        assertEquals(1, actual.getTotalElements());
    }
}