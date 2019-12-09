package com.thejonsey.infrastructure.entity;

import java.util.UUID;
import javax.persistence.*;
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
@Table(name = "log")
public class LogEntity {

    @Id
    private UUID id;
    private String row;
    private Integer count;

    @ManyToOne(fetch = FetchType.EAGER)
    private FileEntity file;

}
