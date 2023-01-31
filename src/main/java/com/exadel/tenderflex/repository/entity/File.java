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
    @Setter
    private String fileKey;
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

        if (getId() != null ? !getId().equals(file.getId()) : file.getId() != null) return false;
        if (getFileType() != file.getFileType()) return false;
        if (getContentType() != null ? !getContentType().equals(file.getContentType()) : file.getContentType() != null)
            return false;
        if (getFileName() != null ? !getFileName().equals(file.getFileName()) : file.getFileName() != null)
            return false;
        if (getUrl() != null ? !getUrl().equals(file.getUrl()) : file.getUrl() != null) return false;
        if (getFileKey() != null ? !getFileKey().equals(file.getFileKey()) : file.getFileKey() != null) return false;
        if (getDtCreate() != null ? !getDtCreate().equals(file.getDtCreate()) : file.getDtCreate() != null)
            return false;
        return getDtUpdate() != null ? getDtUpdate().equals(file.getDtUpdate()) : file.getDtUpdate() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getFileType() != null ? getFileType().hashCode() : 0);
        result = 31 * result + (getContentType() != null ? getContentType().hashCode() : 0);
        result = 31 * result + (getFileName() != null ? getFileName().hashCode() : 0);
        result = 31 * result + (getUrl() != null ? getUrl().hashCode() : 0);
        result = 31 * result + (getFileKey() != null ? getFileKey().hashCode() : 0);
        result = 31 * result + (getDtCreate() != null ? getDtCreate().hashCode() : 0);
        result = 31 * result + (getDtUpdate() != null ? getDtUpdate().hashCode() : 0);
        return result;
    }
}
