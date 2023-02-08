package com.exadel.tenderflex.service;

import com.exadel.tenderflex.core.dto.input.ActionDto;
import com.exadel.tenderflex.core.dto.input.CompanyDetailsDtoInput;
import com.exadel.tenderflex.core.dto.input.ContactPersonDtoInput;
import com.exadel.tenderflex.core.dto.input.OfferDtoInput;
import com.exadel.tenderflex.core.dto.output.*;
import com.exadel.tenderflex.core.dto.output.pages.OfferPageForBidderDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.PageDtoOutput;
import com.exadel.tenderflex.core.mapper.OfferMapper;
import com.exadel.tenderflex.repository.api.IOfferRepository;
import com.exadel.tenderflex.repository.entity.*;
import com.exadel.tenderflex.repository.entity.enums.*;
import com.exadel.tenderflex.service.api.IAwsS3Service;
import com.exadel.tenderflex.service.api.IUserService;
import com.exadel.tenderflex.service.transactional.api.IOfferTransactionalService;
import com.exadel.tenderflex.service.validator.api.IOfferValidator;
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
import java.time.ZoneOffset;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {
    @InjectMocks
    private OfferService offerService;
    @Mock
    private IOfferRepository offerRepository;
    @Mock
    private IOfferValidator offerValidator;
    @Mock
    private OfferMapper offerMapper;
    @Mock
    private IUserService userService;
    @Mock
    private IOfferTransactionalService offerTransactionalService;
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
    final String contentType = "application/pdf";
    final String fileName = "testFile";
    final String url = "http//localhost:8082";
    final Integer maxPrice = 10800;
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
    void save() {
        // preconditions
        final Offer offerInput = getPreparedOfferInput();
        final Offer offerOutput = getPreparedOfferOutput();
        Mockito.when(offerTransactionalService.saveTransactional(offerInput)).thenReturn(offerOutput);

        //test
        Offer actual = offerService.save(offerInput);

        // assert
        assertNotNull(actual);
        checkOfferOutputFields(actual);
    }

    @Test
    void get() {
        // preconditions
        final Offer offerOutput = getPreparedOfferOutput();
        final Pageable pageable = Pageable.ofSize(1).first();
        final Page<Offer> page = new PageImpl<>(Collections.singletonList(offerOutput), pageable, 1);
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(getPreparedUserDetails());
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(offerRepository.findAllForUser(email, pageable)).thenReturn(page);

        //test
        Page<Offer> actual = offerService.get(pageable);

        // assert
        assertNotNull(actual);
        assertEquals(1, actual.getTotalPages());
        Assertions.assertTrue(actual.isFirst());
        for (Offer offer : actual.getContent()) {
            checkOfferOutputFields(offer);
        }
    }

    @Test
    void getForTender() {
        // preconditions
        final Offer offerOutput = getPreparedOfferOutput();
        final Pageable pageable = Pageable.ofSize(1).first();
        final Page<Offer> page = new PageImpl<>(Collections.singletonList(offerOutput), pageable, 1);
        Mockito.when(offerRepository.findAllForTender(id, pageable)).thenReturn(page);

        //test
        Page<Offer> actual = offerService.getForTender(id, pageable);

        // assert
        assertNotNull(actual);
        assertEquals(1, actual.getTotalPages());
        Assertions.assertTrue(actual.isFirst());
        for (Offer offer : actual.getContent()) {
            checkOfferOutputFields(offer);
        }
    }

    @Test
    void getForContractor() {
        // preconditions
        final Offer offerOutput = getPreparedOfferOutput();
        final Pageable pageable = Pageable.ofSize(1).first();
        final Page<Offer> page = new PageImpl<>(Collections.singletonList(offerOutput), pageable, 1);
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(getPreparedUserDetails());
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(offerRepository.findAllForContractor(email, pageable)).thenReturn(page);

        //test
        Page<Offer> actual = offerService.getForContractor(pageable);

        // assert
        assertNotNull(actual);
        assertEquals(1, actual.getTotalPages());
        Assertions.assertTrue(actual.isFirst());
        for (Offer offer : actual.getContent()) {
            checkOfferOutputFields(offer);
        }
    }

    @Test
    void testGet() {
        // preconditions
        final Offer offerOutput = getPreparedOfferOutput();
        Mockito.when(offerRepository.findById(id)).thenReturn(Optional.of(offerOutput));

        //test
        Offer actual = offerService.get(id);

        // assert
        assertNotNull(actual);
        checkOfferOutputFields(actual);
    }

    @Test
    void update() {
        // preconditions
        final Offer offerOutput = getPreparedOfferOutput();
        Mockito.when(offerRepository.findById(id)).thenReturn(Optional.of(offerOutput));
        Mockito.when(offerTransactionalService.saveTransactional(offerOutput)).thenReturn(offerOutput);
        ArgumentCaptor<Long> actualVersion = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Offer> actualOffer = ArgumentCaptor.forClass(Offer.class);

        //test
        Offer actual = offerService.update(offerOutput, id, dtUpdate.toEpochMilli());
        Mockito.verify(offerValidator, Mockito.times(1)).optimisticLockCheck(actualVersion.capture(),
                actualOffer.capture());
        Mockito.verify(offerMapper, Mockito.times(1)).updateEntityFields(actualOffer.capture(),
                actualOffer.capture());

        // assert
        assertEquals(dtUpdate.toEpochMilli(), actualVersion.getValue());
        assertEquals(offerOutput, actualOffer.getValue());
        assertNotNull(actual);
        checkOfferOutputFields(actual);
    }

    @Test
    void getDto() {
        // preconditions
        final Offer offerOutput = getPreparedOfferOutput();
        final Pageable pageable = Pageable.ofSize(1).first();
        final Page<Offer> page = new PageImpl<>(Collections.singletonList(offerOutput), pageable, 1);
        final PageDtoOutput<OfferPageForBidderDtoOutput> pageDtoOutput = getPreparedPageDtoOutput();
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(getPreparedUserDetails());
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(offerRepository.findAllForUser(email, pageable)).thenReturn(page);
        Mockito.when(offerMapper.outputBidderPageMapping(page)).thenReturn(pageDtoOutput);

        //test
        PageDtoOutput<OfferPageForBidderDtoOutput> actual = offerService.getDto(pageable);

        // assert
        assertNotNull(actual);
        checkPageDtoOutputFields(actual);
        for (OfferPageForBidderDtoOutput offer : actual.getContent()) {
            checkOfferPageDtoOutputFields(offer);
        }
    }

    @Test
    void testGetDto() {
        // preconditions
        final Offer offerOutput = getPreparedOfferOutput();
        final OfferDtoOutput offerDtoOutput = getPreparedOfferDtoOutput();
        Mockito.when(offerRepository.findById(id)).thenReturn(Optional.of(offerOutput));
        Mockito.when(offerMapper.outputMapping(offerOutput)).thenReturn(offerDtoOutput);

        //test
        OfferDtoOutput actual = offerService.getDto(id);

        // assert
        assertNotNull(actual);
        checkOfferDtoOutputFields(actual);
    }

    @Test
    void saveDto() {
        // preconditions
        final OfferDtoInput dtoInput = getPreparedOfferDtoInput();
        final OfferDtoOutput dtoOutput = getPreparedOfferDtoOutput();
        final Offer offerInput = getPreparedOfferInput();
        final Offer offerOutput = getPreparedOfferOutput();
        Mockito.when(offerMapper.extractJson(json)).thenReturn(dtoInput);
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(getPreparedUserDetails());
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(userService.getUser(email)).thenReturn(getPreparedUserOutput());
        Mockito.when(offerMapper.inputMapping(any(OfferDtoInput.class), any(User.class), any(Map.class), any(Map.class)))
                .thenReturn(offerInput);
        Mockito.when(offerTransactionalService.saveTransactional(offerInput)).thenReturn(offerOutput);
        Mockito.when(offerMapper.outputMapping(offerOutput)).thenReturn(dtoOutput);
        ArgumentCaptor<Offer> actualOffer = ArgumentCaptor.forClass(Offer.class);

        //test
        OfferDtoOutput actual = offerService.saveDto(json, new HashMap<>());
        Mockito.verify(offerValidator, Mockito.times(1)).validateEntity(actualOffer.capture());
        Mockito.verify(awsS3Service, Mockito.times(1)).generateUrls(any(Map.class));

        // assert
        assertEquals(offerInput, actualOffer.getValue());
        assertNotNull(actual);
        checkOfferDtoOutputFields(actual);
    }

    @Test
    void updateDto() {
        // preconditions
        final OfferDtoInput dtoInput = getPreparedOfferDtoInput();
        final OfferDtoOutput dtoOutput = getPreparedOfferDtoOutput();
        final Offer offerInput = getPreparedOfferInput();
        final Offer offerOutput = getPreparedOfferOutput();
        Mockito.when(offerMapper.extractJson(json)).thenReturn(dtoInput);
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(getPreparedUserDetails());
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(userService.getUser(email)).thenReturn(getPreparedUserOutput());
        Mockito.when(offerMapper.inputMapping(any(OfferDtoInput.class), any(User.class), any(Map.class), any(Map.class)))
                .thenReturn(offerInput);
        Mockito.when(offerRepository.findById(id)).thenReturn(Optional.of(offerInput));
        Mockito.when(offerTransactionalService.saveTransactional(offerInput)).thenReturn(offerOutput);
        Mockito.when(offerMapper.outputMapping(offerOutput)).thenReturn(dtoOutput);
        ArgumentCaptor<Offer> actualOffer = ArgumentCaptor.forClass(Offer.class);
        ArgumentCaptor<Long> actualVersion = ArgumentCaptor.forClass(Long.class);

        //test
        OfferDtoOutput actual = offerService.updateDto(json, new HashMap<>(), id, dtUpdate.toEpochMilli());
        Mockito.verify(offerValidator, Mockito.times(1)).validateEntity(actualOffer.capture());
        Mockito.verify(awsS3Service, Mockito.times(1)).generateUrls(any(Map.class));
        Mockito.verify(offerValidator, Mockito.times(1)).optimisticLockCheck(actualVersion.capture(),
                actualOffer.capture());
        Mockito.verify(offerMapper, Mockito.times(1)).updateEntityFields(actualOffer.capture(),
                actualOffer.capture());

        // assert
        assertEquals(offerInput, actualOffer.getValue());
        assertNotNull(actual);
        checkOfferDtoOutputFields(actual);
    }

    @Test
    void awardAction() {
        // preconditions
        final ActionDto actionDto = getPreparedActionDto();
        final Offer offerOutput = getPreparedOfferOutput();
        final OfferDtoOutput offerDtoOutput = getPreparedOfferDtoOutput();
        Mockito.when(offerTransactionalService.awardTransactionalAction(actionDto)).thenReturn(offerOutput);
        Mockito.when(offerMapper.outputMapping(offerOutput)).thenReturn(offerDtoOutput);

        //test
        OfferDtoOutput actual = offerService.awardAction(actionDto);

        // assert
        assertNotNull(actual);
        checkOfferDtoOutputFields(actual);
    }

    @Test
    void findExpiredContractDeadline() {
        // preconditions
        final Offer offerOutput = getPreparedOfferOutput();
        final LocalDate localDate = LocalDate.now();
        final EOfferStatus status = EOfferStatus.OFFER_SELECTED_BY_CONTRACTOR;
        final Set<Offer> offers = new HashSet<>();
        offers.add(offerOutput);
        Mockito.when(offerRepository.findAllByContract_ContractDeadlineBeforeAndOfferStatusBidder(localDate, status))
                .thenReturn(offers);

        //test
        Set<Offer> actual = offerService.findExpiredContractDeadline(localDate, status);

        // assert
        assertNotNull(actual);
        assertEquals(1, actual.size());
        actual.forEach(this::checkOfferOutputFields);
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

    Offer getPreparedOfferInput() {
        return Offer.builder()
                .user(getPreparedUserOutput())
                .bidder(getPreparedCompanyDetails())
                .contactPerson(getPreparedContactPerson())
                .propositionFile(getPreparedFileOutput())
                .bidPrice(maxPrice)
                .currency(ECurrency.NOK)
                .offerStatusBidder(EOfferStatus.OFFER_SENT)
                .offerStatusContractor(EOfferStatus.OFFER_RECEIVED)
                .build();
    }

    Offer getPreparedOfferOutput() {
        return Offer.builder()
                .id(id)
                .user(getPreparedUserOutput())
                .bidder(getPreparedCompanyDetails())
                .contactPerson(getPreparedContactPerson())
                .propositionFile(getPreparedFileOutput())
                .bidPrice(maxPrice)
                .currency(ECurrency.NOK)
                .offerStatusBidder(EOfferStatus.OFFER_SENT)
                .offerStatusContractor(EOfferStatus.OFFER_RECEIVED)
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate)
                .build();
    }

    UserDetails getPreparedUserDetails() {
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


    OfferDtoOutput getPreparedOfferDtoOutput() {
        return OfferDtoOutput.builder()
                .id(id.toString())
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
                .id(id.toString())
                .user(getPreparedUserLoginDtoOutput())
                .fieldFromTenderCpvCode(cpvCode)
                .officialName(officialName)
                .bidPrice(maxPrice)
                .country(country)
                .dtCreate(dtCreate.atZone(ZoneOffset.UTC).toLocalDate())
                .active(true)
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
                .id(id.toString())
                .fileType(EFileType.PROPOSITION.name())
                .contentType(contentType)
                .fileName(fileName)
                .url(url)
                .dtCreate(dtCreate)
                .dtUpdate(dtUpdate).build();
    }

    CompanyDetailsDtoInput getPreparedCompanyDetailsDtoInput() {
        return CompanyDetailsDtoInput.builder()
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

    OfferDtoInput getPreparedOfferDtoInput() {
        return OfferDtoInput.builder()
                .companyDetails(getPreparedCompanyDetailsDtoInput())
                .contactPerson(getPreparedContactPersonDtoInput())
                .bidPrice(maxPrice)
                .currency(ECurrency.NOK.name())
                .tenderId(id.toString())
                .build();
    }

    ActionDto getPreparedActionDto(){
        return ActionDto.builder()
                .tender(id)
                .offer(id)
                .award(true)
                .build();
    }

    private void checkOfferOutputFields(Offer actual) {
        assertNotNull(actual.getBidder());
        assertNotNull(actual.getContactPerson());
        assertNotNull(actual.getPropositionFile());
        assertEquals(id, actual.getId());
        assertEquals(maxPrice, actual.getBidPrice());
        assertEquals(ECurrency.NOK, actual.getCurrency());
        assertEquals(EOfferStatus.OFFER_SENT, actual.getOfferStatusBidder());
        assertEquals(EOfferStatus.OFFER_RECEIVED, actual.getOfferStatusContractor());
        assertEquals(dtCreate, actual.getDtCreate());
        assertEquals(dtUpdate, actual.getDtUpdate());
        assertEquals(officialName, actual.getBidder().getOfficialName());
        assertEquals(registrationNumber, actual.getBidder().getRegistrationNumber());
        assertEquals(ECountry.POLAND, actual.getBidder().getCountry());
        assertEquals(name, actual.getContactPerson().getName());
        assertEquals(surname, actual.getContactPerson().getSurname());
        assertEquals(phoneNumber, actual.getContactPerson().getPhoneNumber());
        assertEquals(id, actual.getPropositionFile().getId());
        assertEquals(fileName, actual.getPropositionFile().getFileName());
        assertEquals(token, actual.getPropositionFile().getFileKey());
        assertEquals(contentType, actual.getPropositionFile().getContentType());
        assertEquals(url, actual.getPropositionFile().getUrl());
        assertEquals(EFileType.AWARD_DECISION, actual.getPropositionFile().getFileType());
        assertEquals(dtCreate, actual.getPropositionFile().getDtCreate());
        assertEquals(dtUpdate, actual.getPropositionFile().getDtUpdate());
    }

    private void checkOfferDtoOutputFields(OfferDtoOutput actual) {
        assertNotNull(actual.getBidder());
        assertNotNull(actual.getContactPerson());
        assertNotNull(actual.getPropositionFile());
        assertEquals(id.toString(), actual.getId());
        assertEquals(maxPrice, actual.getBidPrice());
        assertEquals(ECurrency.NOK.name(), actual.getCurrency());
        assertEquals(dtCreate, actual.getDtCreate());
        assertEquals(dtUpdate, actual.getDtUpdate());
        assertEquals(officialName, actual.getBidder().getOfficialName());
        assertEquals(registrationNumber, actual.getBidder().getRegistrationNumber());
        assertEquals(ECountry.POLAND.name(), actual.getBidder().getCountry());
        assertEquals(name, actual.getContactPerson().getName());
        assertEquals(surname, actual.getContactPerson().getSurname());
        assertEquals(phoneNumber, actual.getContactPerson().getPhoneNumber());
        assertEquals(id.toString(), actual.getPropositionFile().getId());
        assertEquals(fileName, actual.getPropositionFile().getFileName());
        assertEquals(contentType, actual.getPropositionFile().getContentType());
        assertEquals(url, actual.getPropositionFile().getUrl());
        assertEquals(EFileType.PROPOSITION.name(), actual.getPropositionFile().getFileType());
        assertEquals(dtCreate, actual.getPropositionFile().getDtCreate());
        assertEquals(dtUpdate, actual.getPropositionFile().getDtUpdate());
    }

    private void checkOfferPageDtoOutputFields(OfferPageForBidderDtoOutput actual) {
        assertEquals(id.toString(), actual.getId());
        assertEquals(cpvCode, actual.getFieldFromTenderCpvCode());
        assertEquals(true, actual.getActive());
        assertEquals(maxPrice, actual.getBidPrice());
        assertEquals(country, actual.getCountry());
        assertEquals(dtCreate.atZone(ZoneOffset.UTC).toLocalDate(), actual.getDtCreate());
        assertEquals(officialName, actual.getOfficialName());
        assertEquals(email, actual.getUser().getEmail());
        assertEquals(token, actual.getUser().getToken());
    }

    private void checkPageDtoOutputFields(PageDtoOutput<OfferPageForBidderDtoOutput> actual) {
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