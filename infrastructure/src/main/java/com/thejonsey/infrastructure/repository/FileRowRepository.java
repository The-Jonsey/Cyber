package com.thejonsey.infrastructure.repository;

import com.thejonsey.domain.model.FileRow;
import com.thejonsey.domain.usecase.filerow.DeleteFileRow;
import com.thejonsey.domain.usecase.filerow.GetFileRow;
import com.thejonsey.domain.usecase.filerow.SaveFileRow;
import com.thejonsey.infrastructure.dao.FileRowDao;
import com.thejonsey.infrastructure.mapper.FileRowMapper;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;

@RequiredArgsConstructor
public class FileRowRepository implements SaveFileRow, GetFileRow, DeleteFileRow {

  @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
  private int batch_size;

  private final FileRowDao fileRowDao;

  @Override
  public void saveAll(List<FileRow> fileRowList) {
    for (int i = 0; i < fileRowList.size(); i += batch_size) {
      int j = Math.min(i + batch_size, fileRowList.size());
      List<FileRow> subList = fileRowList.subList(i, j);
      fileRowDao.saveAll(subList.stream().map(FileRowMapper::fromModelToEntity).collect(
          Collectors.toList()));
    }
  }

  @Override
  public List<FileRow> findAllLimit(int limit) {
    PageRequest pageRequest = new PageRequest(0, limit);
    return fileRowDao.findAll(pageRequest).get().map(FileRowMapper::fromEntityToModel).collect(
        Collectors.toList());
  }

  @Override
  public void delete(FileRow fileRow) {
    fileRowDao.delete(FileRowMapper.fromModelToEntity(fileRow));
  }

  @Override
  public void deleteAll(List<FileRow> fileRows) {
    fileRowDao.deleteAll(
        fileRows.stream().map(FileRowMapper::fromModelToEntity).collect(Collectors.toList()));
  }
}
