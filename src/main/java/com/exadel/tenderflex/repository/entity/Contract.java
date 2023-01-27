package com.exadel.tenderflex.repository.entity;

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
@Table(name = "contracts", schema = "app")
public class Contract {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    @OneToOne
    @JoinColumn(name = "tender_id", referencedColumnName = "id", nullable = false)
    @Setter
    private Tender tender;
    @OneToOne
    @JoinColumn(name = "offer_id", referencedColumnName = "id", nullable = false)
    @Setter
    private Offer offer;
    @OneToMany
    @JoinColumn(name = "contract_id", referencedColumnName = "id")
    @Setter
    private Set<File> files;
    @Setter
    @Column(updatable = false)
    private LocalDate contractDeadline;
    @org.hibernate.annotations.Generated(GenerationTime.INSERT)
    private Instant dtCreate;
    @Version
    private Instant dtUpdate;
}
