package com.exadel.tenderflex.repository.entity;

import lombok.*;

import javax.persistence.Embeddable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class ContactPerson {
    private String name;
    private String surname;
    private Long phoneNumber;

    @Override
    public String toString() {
        return "ContactPersonValidator{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", phoneNumber=" + phoneNumber +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactPerson that = (ContactPerson) o;

        if (!getName().equals(that.getName())) return false;
        if (!getSurname().equals(that.getSurname())) return false;
        return getPhoneNumber().equals(that.getPhoneNumber());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getSurname().hashCode();
        result = 31 * result + getPhoneNumber().hashCode();
        return result;
    }
}
