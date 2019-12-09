package com.thejonsey.infrastructure.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "file")
public class FileEntity {

    @Id
    private UUID id;

    private String filename;

    private LocalDateTime uploaded;

    private int rows;

}
