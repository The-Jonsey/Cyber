package com.thejonsey.infrastructure.mapper;

import com.thejonsey.domain.model.Log;
import com.thejonsey.infrastructure.entity.LogEntity;

public class LogMapper {

  public static LogEntity fromModelToEntity(Log log) {
    return new LogEntity(
        log.getId(),
        log.getRow(),
        log.getCount(),
        FileMapper.fromModelToEntity(log.getFile())
    );
  }

  public static Log fromEntityToModel(LogEntity logEntity) {
    return new Log(
        logEntity.getId(),
        logEntity.getRow(),
        logEntity.getCount(),
        FileMapper.fromEntityToModel(logEntity.getFile())
    );
  }

}
