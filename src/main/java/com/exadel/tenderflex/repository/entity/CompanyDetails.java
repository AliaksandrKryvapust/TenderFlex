package com.exadel.tenderflex.repository.entity;

import com.exadel.tenderflex.repository.entity.enums.ECountry;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class CompanyDetails {
    private String officialName;
    @Column(unique = true)
    private String registrationNumber;
    private ECountry country;
    private String town;

    @Override
    public String toString() {
        return "CompanyDetails{" +
                "officialName='" + officialName + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", country=" + country +
                ", town='" + town + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompanyDetails that = (CompanyDetails) o;

        if (!getOfficialName().equals(that.getOfficialName())) return false;
        if (!getRegistrationNumber().equals(that.getRegistrationNumber())) return false;
        if (getCountry() != that.getCountry()) return false;
        return getTown() != null ? getTown().equals(that.getTown()) : that.getTown() == null;
    }

    @Override
    public int hashCode() {
        int result = getOfficialName().hashCode();
        result = 31 * result + getRegistrationNumber().hashCode();
        result = 31 * result + getCountry().hashCode();
        result = 31 * result + (getTown() != null ? getTown().hashCode() : 0);
        return result;
    }
}
