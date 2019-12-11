package com.thejonsey.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class File {

  private UUID id = UUID.randomUUID();

  private final String filename;

  private LocalDateTime uploaded = LocalDateTime.now();

  private final int rows;

  private boolean completed = false;

  public void complete() {
    completed = true;
  }
}
