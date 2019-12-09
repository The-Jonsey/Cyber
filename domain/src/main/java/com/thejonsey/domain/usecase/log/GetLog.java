package com.thejonsey.domain.usecase.log;

import com.thejonsey.domain.model.File;
import com.thejonsey.domain.model.Log;
import java.util.List;
import java.util.Optional;

public interface GetLog {

  List<Log> byFile(File file);

  Optional<Log> byRow(String row);

  List<Log> byRowIn(List<String> rows);

}
