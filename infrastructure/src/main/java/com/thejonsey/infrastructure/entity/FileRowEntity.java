package com.thejonsey.infrastructure.entity;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "filerow")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FileRowEntity implements Persistable<UUID> {

  @Id
  private UUID id;

  @ManyToOne
  private FileEntity file;

  private String row;

  private Long count;

  @Override
  public boolean isNew() {
    return true;
  }
}
