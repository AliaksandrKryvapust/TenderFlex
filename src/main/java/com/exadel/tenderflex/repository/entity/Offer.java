package com.exadel.tenderflex.repository.entity;

import com.exadel.tenderflex.repository.entity.enums.ECurrency;
import com.exadel.tenderflex.repository.entity.enums.EOfferStatus;
import lombok.*;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "offers", schema = "app")
public class Offer {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    @OneToOne
    @JoinColumn(name = "offer_id", referencedColumnName = "id")
    @Setter
    private Contract contract;
    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "offer_id")
    @Setter
    private RejectDecision rejectDecision;
    @Setter
    @Embedded
    private CompanyDetails bidder;
    @Setter
    @Embedded
    private ContactPerson contactPerson;
    @Setter
    private Integer bidPrice;
    @Setter
    @Enumerated(EnumType.STRING)
    private ECurrency currency;
    @Setter
    @Enumerated(EnumType.STRING)
    private EOfferStatus offerStatus;
    @org.hibernate.annotations.Generated(GenerationTime.INSERT)
    private Instant dtCreate;
    @Version
    private Instant dtUpdate;
}
