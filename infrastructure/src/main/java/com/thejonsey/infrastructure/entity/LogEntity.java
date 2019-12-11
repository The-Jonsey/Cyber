package com.thejonsey.infrastructure.entity;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "log")
public class LogEntity implements Persistable<UUID> {

  @Id
  private UUID id;
  private String row;
  @Column(name = "count")
  private int count;

  @ManyToOne(fetch = FetchType.LAZY)
  private FileEntity file;

  @Override
  public boolean isNew() {
    return true;
  }
}
