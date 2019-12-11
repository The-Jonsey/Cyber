package com.thejonsey.application.controller;

import com.thejonsey.application.dto.FileModel;
import com.thejonsey.domain.usecase.file.GetFile;
import com.thejonsey.domain.usecase.file.UploadFile;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

  private final UploadFile uploadFile;
  private final GetFile getFile;

  @GetMapping
  public ResponseEntity getFiles() {
    return ResponseEntity.ok(getFile.getAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity getFile(@PathVariable UUID id) {
    return ResponseEntity.of(getFile.byId(id));
  }

  @PostMapping("/upload")
  public ResponseEntity upload(@Validated FileModel file) {
    MultipartFile multipartFile = file.getFile();
    if (!file.getFile().getOriginalFilename().endsWith("csv")) {
      return ResponseEntity.badRequest().build();
    }
    if (!multipartFile.getContentType().equals("text/csv")) {
      return ResponseEntity.badRequest().build();
    }
    try {
      String content = new String(multipartFile.getBytes(), StandardCharsets.UTF_8);
      content = content.replaceAll("\r", "");
      List<String> rows = Arrays.asList(content.split("\n"));
      return ResponseEntity.status(HttpStatus.ACCEPTED)
          .body(uploadFile.upload(rows, multipartFile.getOriginalFilename()));
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

}
