package com.thejonsey.domain.usecase.log;

import com.thejonsey.domain.model.Log;
import java.util.List;

public interface SaveLog {

  void save(Log log);

  void saveAll(List<Log> logs);

}
