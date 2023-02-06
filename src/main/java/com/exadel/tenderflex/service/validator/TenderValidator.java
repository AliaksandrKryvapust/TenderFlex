package com.exadel.tenderflex.service.validator;

import com.exadel.tenderflex.repository.entity.Tender;
import com.exadel.tenderflex.service.validator.api.ICompanyDetailsValidator;
import com.exadel.tenderflex.service.validator.api.IContactPersonValidator;
import com.exadel.tenderflex.service.validator.api.ITenderValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.OptimisticLockException;
import java.time.Clock;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class TenderValidator implements ITenderValidator {
    private final ICompanyDetailsValidator companyDetailsValidator;
    private final IContactPersonValidator contactPersonValidator;

    @Override
    public void validateEntity(Tender tender) {
        checkAuxiliaryFields(tender);
        checkMandatoryFields(tender);
        companyDetailsValidator.validateEntity(tender.getCompanyDetails());
        contactPersonValidator.validateEntity(tender.getContactPerson());
        checkCpvCode(tender);
        checkTenderType(tender);
        checkDescription(tender);
        checkMinPrice(tender);
        checkMaxPrice(tender);
        checkCurrency(tender);
        checkPublicationDate(tender);
        checkDeadlineDate(tender);
    }

    @Override
    public void optimisticLockCheck(Long version, Tender currentEntity) {
        Long currentVersion = currentEntity.getDtUpdate().toEpochMilli();
        if (!currentVersion.equals(version)) {
            throw new OptimisticLockException("tender table update failed, version does not match update denied");
        }
    }

    private void checkAuxiliaryFields(Tender tender) {
        if (tender.getId() != null) {
            throw new IllegalStateException("Tender id should be empty for tender: " + tender);
        }
        if (tender.getDtUpdate() != null) {
            throw new IllegalStateException("Tender version should be empty for tender: " + tender);
        }
        if (tender.getDtCreate() != null) {
            throw new IllegalStateException("Tender creation date should be empty for tender: " + tender);
        }
    }

    private void checkMandatoryFields(Tender tender) {
        if (tender.getContactPerson() == null) {
            throw new IllegalStateException("Contact person should not be empty for tender: " + tender);
        }
        if (tender.getCompanyDetails() == null) {
            throw new IllegalStateException("Company details should not be empty for tender: " + tender);
        }
        if (tender.getUser() == null) {
            throw new IllegalStateException("User should not be empty for tender: " + tender);
        }
        if (tender.getContract() == null) {
            throw new IllegalStateException("Contract should not be empty for tender: " + tender);
        }
        if (tender.getRejectDecision() == null) {
            throw new IllegalStateException("Reject decision should not be empty for tender: " + tender);
        }
    }

    private void checkCpvCode(Tender tender) {
        if (tender.getCpvCode() == null || tender.getContactPerson().getSurname().isBlank()) {
            throw new IllegalArgumentException("Cpv Code is not valid for tender:" + tender);
        }
    }

    private void checkTenderType(Tender tender) {
        if (tender.getTenderType() == null) {
            throw new IllegalArgumentException("tender type is not valid for tender:" + tender);
        }
    }

    private void checkDescription(Tender tender) {
        if (tender.getDescription() != null) {
            char[] chars = tender.getDescription().toCharArray();
            if (chars.length < 2 || chars.length > 250) {
                throw new IllegalArgumentException("Description should contain from 2 to 250 letters for tender:" + tender);
            }
        }
    }

    private void checkMinPrice(Tender tender) {
        if (tender.getMinPrice() == null) {
            throw new IllegalArgumentException("Min price is not valid for tender:" + tender);
        } else {
            if (tender.getMinPrice()<=0) {
                throw new IllegalArgumentException("Min price should be positive for tender:" + tender);
            }
        }
    }

    private void checkMaxPrice(Tender tender) {
        if (tender.getMaxPrice() == null) {
            throw new IllegalArgumentException("Max price is not valid for tender:" + tender);
        } else {
            if (tender.getMaxPrice()<=tender.getMinPrice()) {
                throw new IllegalArgumentException("Max price should be greater than min price for tender:" + tender);
            }
        }
    }

    private void checkCurrency(Tender tender) {
        if (tender.getCurrency() == null) {
            throw new IllegalArgumentException("currency is not valid for tender:" + tender);
        }
    }

    private void checkPublicationDate(Tender tender) {
        if (tender.getPublication() == null) {
            throw new IllegalArgumentException("Publication Date is not valid for tender:" + tender);
        } else {
            LocalDate currentDate = LocalDate.now(Clock.systemUTC());
            if (tender.getPublication().isAfter(currentDate)) {
                throw new IllegalArgumentException("Publication Date should refer to moment in the past for tender:" + tender);
            }
        }
    }

    private void checkDeadlineDate(Tender tender) {
        if (tender.getSubmissionDeadline() == null) {
            throw new IllegalArgumentException("Deadline Date is not valid for tender:" + tender);
        } else {
            if (tender.getSubmissionDeadline().isBefore(tender.getPublication().plusDays(1))) {
                throw new IllegalArgumentException("Deadline Date should be at least 1 day after publication for tender:" + tender);
            }
        }
    }
}
