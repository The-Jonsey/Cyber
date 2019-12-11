package com.thejonsey.domain.usecase.log;

import com.thejonsey.domain.model.File;
import com.thejonsey.domain.model.Log;
import com.thejonsey.domain.model.SlimLog;
import java.util.List;
import java.util.Optional;

public interface GetLog {

  List<Log> byFile(File file, int page, int size, String sort, boolean asc);

  List<Log> getAll(int page, int size, String sort, boolean asc);

  List<SlimLog> getAllGrouped(List<File> files, int page, int size);

  Optional<Log> byRow(String row);

  List<Log> byRowIn(List<String> rows);

}
