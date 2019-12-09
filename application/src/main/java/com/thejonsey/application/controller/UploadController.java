package com.thejonsey.application.controller;

import com.thejonsey.application.dto.FileModel;
import com.thejonsey.domain.usecase.file.UploadFile;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UploadController {

  private final UploadFile uploadFile;

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
