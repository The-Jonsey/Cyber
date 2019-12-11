package com.thejonsey.infrastructure.repository;

import com.thejonsey.domain.model.File;
import com.thejonsey.domain.model.Log;
import com.thejonsey.domain.model.SlimLog;
import com.thejonsey.domain.usecase.log.GetLog;
import com.thejonsey.domain.usecase.log.SaveLog;
import com.thejonsey.infrastructure.dao.LogDao;
import com.thejonsey.infrastructure.mapper.FileMapper;
import com.thejonsey.infrastructure.mapper.LogMapper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
public class LogRepository implements GetLog, SaveLog {

  private final LogDao logDao;

  @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
  private int batch_size;

  @Override
  public List<Log> byFile(File file, int page, int size, String sort, boolean asc) {
    return logDao.findAllByFile(FileMapper.fromModelToEntity(file),
        PageRequest.of(page, size, generateSort(sort, asc))).stream()
        .map(LogMapper::fromEntityToModel)
        .collect(Collectors.toList());
  }

  @Override
  public List<Log> getAll(int page, int size, String sort, boolean asc) {
    return logDao.findAll(PageRequest.of(page, size, generateSort(sort, asc))).stream()
        .map(LogMapper::fromEntityToModel).collect(Collectors.toList());
  }

  @Override
  public List<SlimLog> getAllGrouped(List<File> files, int page, int size) {
    return logDao.findAllGrouped(
        files.stream().map(FileMapper::fromModelToEntity).collect(Collectors.toList()),
        PageRequest.of(page, size)).stream()
        .map(LogMapper::fromEntityToModel).collect(Collectors.toList());
  }

  @Override
  public Optional<Log> byRow(String row) {
    return logDao.findByRow(row).map(LogMapper::fromEntityToModel);
  }

  @Override
  public List<Log> byRowIn(List<String> rows) {
    return logDao.findAllByRowIn(rows).stream().map(LogMapper::fromEntityToModel)
        .collect(Collectors.toList());
  }

  @Override
  public void save(Log log) {
    logDao.save(LogMapper.fromModelToEntity(log));
  }

  @Override
  public void saveAll(List<Log> logs) {
    for (int i = 0; i < logs.size(); i += batch_size) {
      int j = Math.min(i + batch_size, logs.size());
      List<Log> subList = logs.subList(i, j);
      logDao.saveAll(subList.stream().map(LogMapper::fromModelToEntity).collect(
          Collectors.toList()));
    }
  }

  private Sort generateSort(String sort, boolean asc) {
    if (sort == null) {
      return Sort.by("count").descending();
    }
    if (asc) {
      return Sort.by(sort).ascending();
    } else {
      return Sort.by(sort).descending();
    }
  }
}
