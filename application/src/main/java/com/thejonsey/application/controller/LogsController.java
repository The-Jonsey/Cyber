package com.thejonsey.application.controller;

import com.thejonsey.domain.model.File;
import com.thejonsey.domain.usecase.file.GetFile;
import com.thejonsey.domain.usecase.log.GetLog;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class LogsController {

  private final GetFile getFile;
  private final GetLog getLog;

  @GetMapping("/all")
  public ResponseEntity getAll(@RequestParam int page, @RequestParam int size,
      @RequestParam(required = false) String sort, @RequestParam(required = false) boolean asc) {
    return ResponseEntity.ok(getLog.getAll(page, size, sort, asc));
  }

  @GetMapping("/{id}")
  public ResponseEntity getByFile(@PathVariable UUID id, @RequestParam int page, @RequestParam int size,
      @RequestParam(required = false) String sort, @RequestParam(required = false) boolean asc) {
    Optional<File> file = getFile.byId(id);
    if (!file.isPresent()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(getLog.byFile(file.get(), page, size, sort, asc));
  }

  @GetMapping("/files")
  public ResponseEntity getByFiles(@RequestParam List<UUID> ids, @RequestParam int page,
      @RequestParam int size) {
    List<File> files = getFile.byIds(ids);
    if (files.size() != ids.size()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(getLog.getAllGrouped(files, page, size));
  }

}
