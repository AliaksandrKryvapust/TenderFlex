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
}
