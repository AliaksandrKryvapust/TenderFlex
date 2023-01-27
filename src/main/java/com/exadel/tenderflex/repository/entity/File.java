package com.exadel.tenderflex.repository.entity;

import com.exadel.tenderflex.repository.entity.enums.EFileType;
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
@Table(name = "files", schema = "app")
public class File {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    @Setter
    @Enumerated(EnumType.STRING)
    private EFileType fileType;
    @Setter
    private String contentType;
    @Setter
    private String fileName;
    @Setter
    private String url;
    @org.hibernate.annotations.Generated(GenerationTime.INSERT)
    private Instant dtCreate;
    @Version
    private Instant dtUpdate;

    @Override
    public String toString() {
        return "File{" +
                "id=" + id +
                ", fileType=" + fileType +
                ", contentType='" + contentType + '\'' +
                ", fileName='" + fileName + '\'' +
                ", url='" + url + '\'' +
                ", dtCreate=" + dtCreate +
                ", dtUpdate=" + dtUpdate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        File file = (File) o;

        if (!getId().equals(file.getId())) return false;
        if (getFileType() != file.getFileType()) return false;
        if (!getContentType().equals(file.getContentType())) return false;
        if (!getFileName().equals(file.getFileName())) return false;
        if (!getUrl().equals(file.getUrl())) return false;
        if (!getDtCreate().equals(file.getDtCreate())) return false;
        return getDtUpdate().equals(file.getDtUpdate());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getFileType().hashCode();
        result = 31 * result + getContentType().hashCode();
        result = 31 * result + getFileName().hashCode();
        result = 31 * result + getUrl().hashCode();
        result = 31 * result + getDtCreate().hashCode();
        result = 31 * result + getDtUpdate().hashCode();
        return result;
    }
}
