package com.thejonsey.domain.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Getter
@RequiredArgsConstructor
public class FileRow {

  private UUID id = UUID.randomUUID();

  private final File file;

  private final String row;

  private final Long count;

}
