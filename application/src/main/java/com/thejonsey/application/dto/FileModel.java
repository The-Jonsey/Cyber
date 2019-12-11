package com.thejonsey.application.dto;

import org.springframework.web.multipart.MultipartFile;


/**
 * Class to pull file from POST request
 */
public class FileModel {

  private MultipartFile file;

  public MultipartFile getFile() {
    return file;
  }

  public void setFile(MultipartFile file) {
    this.file = file;
  }
}