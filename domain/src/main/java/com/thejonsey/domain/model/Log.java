package com.thejonsey.domain.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Log {

  public Log(String row, Integer count, File file) {
    this.row = row;
    this.count = count;
    this.file = file;
  }

  private UUID id = UUID.randomUUID();

  private String row;

  private Integer count;

  private File file;
}
