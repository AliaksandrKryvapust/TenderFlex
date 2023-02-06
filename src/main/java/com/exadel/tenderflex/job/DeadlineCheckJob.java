package com.exadel.tenderflex.job;

import com.exadel.tenderflex.repository.entity.Offer;
import com.exadel.tenderflex.repository.entity.Tender;
import com.exadel.tenderflex.repository.entity.enums.EOfferStatus;
import com.exadel.tenderflex.repository.entity.enums.ETenderStatus;
import com.exadel.tenderflex.service.api.IOfferService;
import com.exadel.tenderflex.service.api.ITenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeadlineCheckJob implements Job {
    private final ITenderService tenderService;
    private final IOfferService offerService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        log.info("Deadline Job started");
        LocalDate currentDate = LocalDate.now(ZoneOffset.UTC);
        checkSubmissionExpiration(currentDate);
        checkContractExpiration(currentDate);
    }

    private void checkContractExpiration(LocalDate currentDate) {
        Set<Offer> tenderWithExpiredContract = offerService.findExpiredContractDeadline(currentDate, EOfferStatus.OFFER_SELECTED_BY_CONTRACTOR);
        if (!tenderWithExpiredContract.isEmpty()){
            tenderWithExpiredContract.forEach((i)->{
                i.setOfferStatusBidder(EOfferStatus.CONTRACT_DECLINED_BY_BIDDER);
                i.setOfferStatusContractor(EOfferStatus.CONTRACT_DECLINED_BY_BIDDER);
                i.setActive(false);
                offerService.save(i);
            });
        }
    }

    private void checkSubmissionExpiration(LocalDate currentDate) {
        Set<Tender> tendersWithExpiredSubmission = tenderService.findExpiredSubmissionDeadline(currentDate, ETenderStatus.IN_PROGRESS);
        if (!tendersWithExpiredSubmission.isEmpty()) {
            tendersWithExpiredSubmission.forEach((i) -> {
                if (i.getOffers().size()==0) {
                    i.setTenderStatus(ETenderStatus.CLOSED);
                    tenderService.save(i);
                }
            });
        }
    }

}
