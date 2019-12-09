package com.thejonsey.infrastructure.mapper;

import com.thejonsey.domain.model.FileRow;
import com.thejonsey.domain.model.Log;
import com.thejonsey.infrastructure.entity.FileRowEntity;

public class FileRowMapper {

  public static FileRowEntity fromModelToEntity(FileRow fileRow) {
    return new FileRowEntity(
        fileRow.getId(),
        FileMapper.fromModelToEntity(fileRow.getFile()),
        fileRow.getRow(),
        fileRow.getCount()
    );
  }

  public static FileRow fromEntityToModel(FileRowEntity fileRowEntity) {
    return new FileRow(
        fileRowEntity.getId(),
        FileMapper.fromEntityToModel(fileRowEntity.getFile()),
        fileRowEntity.getRow(),
        fileRowEntity.getCount()
    );
  }

}
