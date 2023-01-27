package com.exadel.tenderflex.repository.entity;

import lombok.*;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "reject_decision", schema = "app")
public class RejectDecision {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    @OneToOne
    @JoinColumn(name = "tender_id", referencedColumnName = "id", nullable = false)
    @Setter
    private Tender tender;
    @OneToMany
    @JoinColumn(name = "reject_decision_id", referencedColumnName = "id")
    @Setter
    private Set<Offer> offers;
    @OneToOne
    @JoinColumn(name = "file_id", referencedColumnName = "id", nullable = false)
    @Setter
    private File file;
    @org.hibernate.annotations.Generated(GenerationTime.INSERT)
    private Instant dtCreate;
    @Version
    private Instant dtUpdate;
}
