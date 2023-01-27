package com.exadel.tenderflex.repository.entity;

import com.exadel.tenderflex.repository.entity.enums.ECurrency;
import com.exadel.tenderflex.repository.entity.enums.ETenderStatus;
import com.exadel.tenderflex.repository.entity.enums.ETenderType;
import lombok.*;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tenders", schema = "app")
public class Tender {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    @Column(name = "user_id", insertable = false, updatable = false)
    private UUID userId;
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @Setter
    private User user;
    @OneToOne(mappedBy = "tender")
    @Setter
    private Contract contract;
    @OneToOne(mappedBy = "tender")
    @Setter
    private RejectDecision rejectDecision;
    @OneToMany
    @JoinColumn(name = "tender_id", referencedColumnName = "id", nullable = false)
    @Setter
    private Set<Offer> offers;
    @Setter
    @Embedded
    private CompanyDetails companyDetails;
    @Setter
    @Embedded
    private ContactPerson contactPerson;
    @Setter
    private String cpvCode;
    @Setter
    @Enumerated(EnumType.STRING)
    private ETenderType tenderType;
    @Setter
    private String description;
    @Setter
    private Integer minPrice;
    @Setter
    private Integer maxPrice;
    @Setter
    @Enumerated(EnumType.STRING)
    private ECurrency currency;
    @Setter
    @Column(updatable = false)
    private LocalDate publication;
    @Setter
    private LocalDate submissionDeadline;
    @Setter
    @Enumerated(EnumType.STRING)
    private ETenderStatus tenderStatus;
    @org.hibernate.annotations.Generated(GenerationTime.INSERT)
    private Instant dtCreate;
    @Version
    private Instant dtUpdate;

    @Override
    public String toString() {
        return "Tender{" +
                "id=" + id +
                ", companyDetails=" + companyDetails +
                ", contactPerson=" + contactPerson +
                ", cpvCode='" + cpvCode + '\'' +
                ", tenderType=" + tenderType +
                ", description='" + description + '\'' +
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                ", currency=" + currency +
                ", publication=" + publication +
                ", submissionDeadline=" + submissionDeadline +
                ", tenderStatus=" + tenderStatus +
                ", dtCreate=" + dtCreate +
                ", dtUpdate=" + dtUpdate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tender tender = (Tender) o;

        if (!getId().equals(tender.getId())) return false;
        if (!getCompanyDetails().equals(tender.getCompanyDetails())) return false;
        if (!getContactPerson().equals(tender.getContactPerson())) return false;
        if (!getCpvCode().equals(tender.getCpvCode())) return false;
        if (getTenderType() != tender.getTenderType()) return false;
        if (!getDescription().equals(tender.getDescription())) return false;
        if (!getMinPrice().equals(tender.getMinPrice())) return false;
        if (!getMaxPrice().equals(tender.getMaxPrice())) return false;
        if (getCurrency() != tender.getCurrency()) return false;
        if (!getPublication().equals(tender.getPublication())) return false;
        if (!getSubmissionDeadline().equals(tender.getSubmissionDeadline())) return false;
        if (getTenderStatus() != tender.getTenderStatus()) return false;
        if (!getDtCreate().equals(tender.getDtCreate())) return false;
        return getDtUpdate().equals(tender.getDtUpdate());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getCompanyDetails().hashCode();
        result = 31 * result + getContactPerson().hashCode();
        result = 31 * result + getCpvCode().hashCode();
        result = 31 * result + getTenderType().hashCode();
        result = 31 * result + getDescription().hashCode();
        result = 31 * result + getMinPrice().hashCode();
        result = 31 * result + getMaxPrice().hashCode();
        result = 31 * result + getCurrency().hashCode();
        result = 31 * result + getPublication().hashCode();
        result = 31 * result + getSubmissionDeadline().hashCode();
        result = 31 * result + getTenderStatus().hashCode();
        result = 31 * result + getDtCreate().hashCode();
        result = 31 * result + getDtUpdate().hashCode();
        return result;
    }
}
