package com.thejonsey.infrastructure.repository;

import com.thejonsey.domain.model.File;
import com.thejonsey.domain.model.FileRow;
import com.thejonsey.domain.model.Log;
import com.thejonsey.domain.usecase.log.GetLog;
import com.thejonsey.domain.usecase.log.SaveLog;
import com.thejonsey.infrastructure.dao.LogDao;
import com.thejonsey.infrastructure.mapper.FileMapper;
import com.thejonsey.infrastructure.mapper.FileRowMapper;
import com.thejonsey.infrastructure.mapper.LogMapper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@RequiredArgsConstructor
public class LogRepository implements GetLog, SaveLog {

  private final LogDao logDao;

  @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
  private int batch_size;

  @Override
  public List<Log> byFile(File file) {
    return logDao.findAllByFile(FileMapper.fromModelToEntity(file)).stream()
        .map(LogMapper::fromEntityToModel)
        .collect(Collectors.toList());
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
}
