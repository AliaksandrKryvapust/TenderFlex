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
    @OneToMany(mappedBy="tender")
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

        if (getId() != null ? !getId().equals(tender.getId()) : tender.getId() != null) return false;
        if (!getCompanyDetails().equals(tender.getCompanyDetails())) return false;
        if (!getContactPerson().equals(tender.getContactPerson())) return false;
        if (!getCpvCode().equals(tender.getCpvCode())) return false;
        if (getTenderType() != tender.getTenderType()) return false;
        if (getDescription() != null ? !getDescription().equals(tender.getDescription()) : tender.getDescription() != null)
            return false;
        if (!getMinPrice().equals(tender.getMinPrice())) return false;
        if (!getMaxPrice().equals(tender.getMaxPrice())) return false;
        if (getCurrency() != tender.getCurrency()) return false;
        if (!getPublication().equals(tender.getPublication())) return false;
        if (!getSubmissionDeadline().equals(tender.getSubmissionDeadline())) return false;
        if (getTenderStatus() != tender.getTenderStatus()) return false;
        if (getDtCreate() != null ? !getDtCreate().equals(tender.getDtCreate()) : tender.getDtCreate() != null)
            return false;
        return getDtUpdate() != null ? getDtUpdate().equals(tender.getDtUpdate()) : tender.getDtUpdate() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + getCompanyDetails().hashCode();
        result = 31 * result + getContactPerson().hashCode();
        result = 31 * result + getCpvCode().hashCode();
        result = 31 * result + getTenderType().hashCode();
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + getMinPrice().hashCode();
        result = 31 * result + getMaxPrice().hashCode();
        result = 31 * result + getCurrency().hashCode();
        result = 31 * result + getPublication().hashCode();
        result = 31 * result + getSubmissionDeadline().hashCode();
        result = 31 * result + getTenderStatus().hashCode();
        result = 31 * result + (getDtCreate() != null ? getDtCreate().hashCode() : 0);
        result = 31 * result + (getDtUpdate() != null ? getDtUpdate().hashCode() : 0);
        return result;
    }
}
