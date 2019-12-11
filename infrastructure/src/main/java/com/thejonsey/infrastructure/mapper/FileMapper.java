package com.thejonsey.infrastructure.mapper;

import com.thejonsey.domain.model.File;
import com.thejonsey.infrastructure.entity.FileEntity;

public class FileMapper {

  public static FileEntity fromModelToEntity(File file) {
    return new FileEntity(
        file.getId(),
        file.getFilename(),
        file.getUploaded(),
        file.getRows(),
        file.isCompleted()
    );
  }

  public static File fromEntityToModel(FileEntity fileEntity) {
    return new File(
        fileEntity.getId(),
        fileEntity.getFilename(),
        fileEntity.getUploaded(),
        fileEntity.getRows(),
        fileEntity.isCompleted()
    );
  }

}
