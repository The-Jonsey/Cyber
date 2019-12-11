package com.thejonsey.infrastructure.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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

  private boolean completed;

}
