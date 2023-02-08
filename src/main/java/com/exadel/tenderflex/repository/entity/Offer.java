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
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @Setter
    private User user;
    @Setter
    @ManyToOne
    @JoinColumn(name = "tender_id", insertable = false, updatable = false)
    private Tender tender;
    @Setter
    @Embedded
    private CompanyDetails bidder;
    @Setter
    @Embedded
    private ContactPerson contactPerson;
    @OneToOne(mappedBy = "offer")
    @Setter
    private Contract contract;
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "file_id", referencedColumnName = "id", nullable = false)
    @Setter
    private File propositionFile;
    @Column(name = "tender_id", insertable = false, updatable = false)
    private UUID tenderId;
    @Setter
    private Integer bidPrice;
    @Setter
    @Enumerated(EnumType.STRING)
    private ECurrency currency;
    @Setter
    @Enumerated(EnumType.STRING)
    private EOfferStatus offerStatusBidder;
    @Setter
    @Enumerated(EnumType.STRING)
    private EOfferStatus offerStatusContractor;
    @Setter
    private Boolean active;
    @org.hibernate.annotations.Generated(GenerationTime.INSERT)
    private Instant dtCreate;
    @Version
    private Instant dtUpdate;

    @Override
    public String toString() {
        return "Offer{" +
                "id=" + id +
                ", bidder=" + bidder +
                ", contactPerson=" + contactPerson +
                ", tenderId=" + tenderId +
                ", bidPrice=" + bidPrice +
                ", currency=" + currency +
                ", offerStatusBidder=" + offerStatusBidder +
                ", offerStatusContractor=" + offerStatusContractor +
                ", dtCreate=" + dtCreate +
                ", dtUpdate=" + dtUpdate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Offer offer = (Offer) o;

        if (getId() != null ? !getId().equals(offer.getId()) : offer.getId() != null) return false;
        if (!getBidder().equals(offer.getBidder())) return false;
        if (!getContactPerson().equals(offer.getContactPerson())) return false;
        if (getTenderId() != null ? !getTenderId().equals(offer.getTenderId()) : offer.getTenderId() != null)
            return false;
        if (!getBidPrice().equals(offer.getBidPrice())) return false;
        if (getCurrency() != offer.getCurrency()) return false;
        if (getOfferStatusBidder() != offer.getOfferStatusBidder()) return false;
        if (getOfferStatusContractor() != offer.getOfferStatusContractor()) return false;
        if (getDtCreate() != null ? !getDtCreate().equals(offer.getDtCreate()) : offer.getDtCreate() != null)
            return false;
        return getDtUpdate() != null ? getDtUpdate().equals(offer.getDtUpdate()) : offer.getDtUpdate() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + getBidder().hashCode();
        result = 31 * result + getContactPerson().hashCode();
        result = 31 * result + (getTenderId() != null ? getTenderId().hashCode() : 0);
        result = 31 * result + getBidPrice().hashCode();
        result = 31 * result + getCurrency().hashCode();
        result = 31 * result + getOfferStatusBidder().hashCode();
        result = 31 * result + getOfferStatusContractor().hashCode();
        result = 31 * result + (getDtCreate() != null ? getDtCreate().hashCode() : 0);
        result = 31 * result + (getDtUpdate() != null ? getDtUpdate().hashCode() : 0);
        return result;
    }
}
