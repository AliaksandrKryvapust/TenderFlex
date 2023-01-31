package com.exadel.tenderflex.service.validator;

import com.exadel.tenderflex.repository.entity.Tender;
import com.exadel.tenderflex.service.validator.api.ITenderValidator;
import org.springframework.stereotype.Component;

import javax.persistence.OptimisticLockException;
import java.time.Clock;
import java.time.LocalDate;

@Component
public class TenderValidator implements ITenderValidator {
    @Override
    public void validateEntity(Tender tender) {
    checkAuxiliaryFields(tender);
    checkMandatoryFields(tender);
    checkOfficialName(tender);
    checkRegistrationNumber(tender);
    checkCountry(tender);
    checkTown(tender);
    checkName(tender);
    checkSurname(tender);
    checkPhoneNumber(tender);
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

    private void checkOfficialName(Tender tender) {
        if (tender.getCompanyDetails().getOfficialName() == null || tender.getCompanyDetails().getOfficialName().isBlank()) {
            throw new IllegalArgumentException("Official name is not valid for tender:" + tender);
        } else {
            char[] chars = tender.getCompanyDetails().getOfficialName().toCharArray();
            if (chars.length < 2 || chars.length > 50) {
                throw new IllegalArgumentException("Official name should contain from 2 to 50 letters for tender:" + tender);
            }
        }
    }

    private void checkRegistrationNumber(Tender tender) {
        if (tender.getCompanyDetails().getRegistrationNumber() == null || tender.getCompanyDetails().getRegistrationNumber().isBlank()) {
            throw new IllegalArgumentException("Registration number is not valid for tender:" + tender);
        } else {
            char[] chars = tender.getCompanyDetails().getRegistrationNumber().toCharArray();
            if (chars.length < 2 || chars.length > 50) {
                throw new IllegalArgumentException("Registration number should contain from 2 to 50 letters for tender:" + tender);
            }
        }
    }

    private void checkCountry(Tender tender) {
        if (tender.getCompanyDetails().getCountry() == null) {
            throw new IllegalArgumentException("tender country is not valid for tender:" + tender);
        }
    }

    private void checkTown(Tender tender) {
        if (tender.getCompanyDetails().getTown() != null) {
            char[] chars = tender.getCompanyDetails().getTown().toCharArray();
            if (chars.length < 2 || chars.length > 50) {
                throw new IllegalArgumentException("Town should contain from 2 to 50 letters for tender:" + tender);
            }
        }
    }

    private void checkName(Tender tender) {
        if (tender.getContactPerson().getName() == null || tender.getContactPerson().getName().isBlank()) {
            throw new IllegalArgumentException("Name is not valid for tender:" + tender);
        } else {
            char[] chars = tender.getContactPerson().getName().toCharArray();
            if (chars.length < 2 || chars.length > 50) {
                throw new IllegalArgumentException("Name should contain from 2 to 50 letters for tender:" + tender);
            }
        }
    }

    private void checkSurname(Tender tender) {
        if (tender.getContactPerson().getSurname() == null || tender.getContactPerson().getSurname().isBlank()) {
            throw new IllegalArgumentException("Surname is not valid for tender:" + tender);
        } else {
            char[] chars = tender.getContactPerson().getSurname().toCharArray();
            if (chars.length < 2 || chars.length > 50) {
                throw new IllegalArgumentException("Surname should contain from 2 to 50 letters for tender:" + tender);
            }
        }
    }

    private void checkPhoneNumber(Tender tender) {
        if (tender.getContactPerson().getPhoneNumber() == null) {
            throw new IllegalArgumentException("Phone number is not valid for tender:" + tender);
        } else {
            if (tender.getContactPerson().getPhoneNumber()<=0) {
                throw new IllegalArgumentException("Phone number should be positive for tender:" + tender);
            }
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
