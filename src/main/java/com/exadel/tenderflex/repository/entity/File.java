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
}
