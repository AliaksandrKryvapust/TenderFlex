package com.exadel.tenderflex.repository.entity;

import com.exadel.tenderflex.repository.entity.enums.ERolePrivilege;
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
@Table(name = "privileges", schema = "app")
public class Privilege {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    @Setter
    @Enumerated(EnumType.STRING)
    private ERolePrivilege privilege;
    @org.hibernate.annotations.Generated(GenerationTime.INSERT)
    private Instant dtCreate;
    @Version
    private Instant dtUpdate;

    @Override
    public String toString() {
        return "Privilege{" +
                "id=" + id +
                ", privilege=" + privilege +
                ", dtCreate=" + dtCreate +
                ", dtUpdate=" + dtUpdate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Privilege privilege1 = (Privilege) o;

        if (!getId().equals(privilege1.getId())) return false;
        if (getPrivilege() != privilege1.getPrivilege()) return false;
        if (!getDtCreate().equals(privilege1.getDtCreate())) return false;
        return getDtUpdate().equals(privilege1.getDtUpdate());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getPrivilege().hashCode();
        result = 31 * result + getDtCreate().hashCode();
        result = 31 * result + getDtUpdate().hashCode();
        return result;
    }
}
