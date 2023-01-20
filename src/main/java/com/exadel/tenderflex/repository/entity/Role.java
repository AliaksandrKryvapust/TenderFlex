package com.exadel.tenderflex.repository.entity;

import lombok.*;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "roles", schema = "app")
public class Role {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    @Setter
    @Enumerated(EnumType.STRING)
    private EUserRole roleType;
    @Setter
    @ManyToMany
    @JoinTable(name = "role_privilege", joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id", referencedColumnName = "id"))
    private List<Privilege> privileges;
    @org.hibernate.annotations.Generated(GenerationTime.INSERT)
    private Instant dtCreate;
    @Version
    private Instant dtUpdate;

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", role=" + roleType +
                ", dtCreate=" + dtCreate +
                ", dtUpdate=" + dtUpdate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Role role1 = (Role) o;

        if (!getId().equals(role1.getId())) return false;
        if (getRoleType() != role1.getRoleType()) return false;
        if (!getDtCreate().equals(role1.getDtCreate())) return false;
        return getDtUpdate().equals(role1.getDtUpdate());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getRoleType().hashCode();
        result = 31 * result + getDtCreate().hashCode();
        result = 31 * result + getDtUpdate().hashCode();
        return result;
    }
}
