package com.exadel.tenderflex.service.api;

import com.exadel.tenderflex.repository.entity.Tender;
import com.exadel.tenderflex.repository.entity.enums.ETenderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Set;

public interface ITenderService extends IService<Tender>, IServiceUpdate<Tender> {
    Tender saveInTransaction(Tender tender);
    Page<Tender> getAll(Pageable pageable);
    Set<Tender> findExpiredSubmissionDeadline(LocalDate currentDate, ETenderStatus tenderStatus);
}
