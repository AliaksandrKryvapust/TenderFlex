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
    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "contract_id", referencedColumnName = "id", nullable = false)
    @Setter
    private Set<File> files;
    @Setter
    @Column(updatable = false)
    private LocalDate contractDeadline;
    @org.hibernate.annotations.Generated(GenerationTime.INSERT)
    private Instant dtCreate;
    @Version
    private Instant dtUpdate;

    @Override
    public String toString() {
        return "Contract{" +
                "id=" + id +
                ", contractDeadline=" + contractDeadline +
                ", dtCreate=" + dtCreate +
                ", dtUpdate=" + dtUpdate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contract contract = (Contract) o;

        if (getId() != null ? !getId().equals(contract.getId()) : contract.getId() != null) return false;
        if (!getContractDeadline().equals(contract.getContractDeadline())) return false;
        if (getDtCreate() != null ? !getDtCreate().equals(contract.getDtCreate()) : contract.getDtCreate() != null)
            return false;
        return getDtUpdate() != null ? getDtUpdate().equals(contract.getDtUpdate()) : contract.getDtUpdate() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + getContractDeadline().hashCode();
        result = 31 * result + (getDtCreate() != null ? getDtCreate().hashCode() : 0);
        result = 31 * result + (getDtUpdate() != null ? getDtUpdate().hashCode() : 0);
        return result;
    }
}
