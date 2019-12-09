package com.thejonsey.infrastructure.repository;

import com.thejonsey.domain.model.File;
import com.thejonsey.domain.usecase.file.GetFile;
import com.thejonsey.domain.usecase.file.SaveFile;
import com.thejonsey.infrastructure.dao.FileDao;
import com.thejonsey.infrastructure.mapper.FileMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileRepository implements GetFile, SaveFile {

  private final FileDao fileDao;

  @Override
  public Optional<File> byId(UUID id) {
    return fileDao.findById(id).map(FileMapper::fromEntityToModel);
  }

  @Override
  public List<File> byFilename(String filename) {
    return fileDao.findAllByFilename(filename).stream().map(FileMapper::fromEntityToModel)
        .collect(Collectors.toList());
  }

  @Override
  public File save(File file) {
    return FileMapper.fromEntityToModel(fileDao.save(FileMapper.fromModelToEntity(file)));
  }
}
