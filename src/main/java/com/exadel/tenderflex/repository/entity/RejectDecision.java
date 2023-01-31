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
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "file_id", referencedColumnName = "id", nullable = false)
    @Setter
    private File file;
    @org.hibernate.annotations.Generated(GenerationTime.INSERT)
    private Instant dtCreate;
    @Version
    private Instant dtUpdate;

    @Override
    public String toString() {
        return "RejectDecision{" +
                "id=" + id +
                ", dtCreate=" + dtCreate +
                ", dtUpdate=" + dtUpdate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RejectDecision that = (RejectDecision) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getDtCreate() != null ? !getDtCreate().equals(that.getDtCreate()) : that.getDtCreate() != null)
            return false;
        return getDtUpdate() != null ? getDtUpdate().equals(that.getDtUpdate()) : that.getDtUpdate() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getDtCreate() != null ? getDtCreate().hashCode() : 0);
        result = 31 * result + (getDtUpdate() != null ? getDtUpdate().hashCode() : 0);
        return result;
    }
}
