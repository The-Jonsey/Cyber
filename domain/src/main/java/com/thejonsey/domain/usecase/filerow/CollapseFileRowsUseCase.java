package com.thejonsey.domain.usecase.filerow;

import com.thejonsey.domain.model.FileRow;
import com.thejonsey.domain.model.Log;
import com.thejonsey.domain.usecase.log.GetLog;
import com.thejonsey.domain.usecase.log.SaveLog;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CollapseFileRowsUseCase implements CollapseFileRows {

  private final GetFileRow getFileRow;
  private final DeleteFileRow deleteFileRow;
  private final GetLog getLog;
  private final SaveLog saveLog;

  @Override
  public void collapse() {
    List<FileRow> rows = getFileRow.findAllLimit(25000);
    List<Log> logs = getLog
        .byRowIn(rows.stream().map(FileRow::getRow).collect(Collectors.toList()));
    Map<String, List<Log>> map = logs.stream()
        .collect(Collectors.groupingBy(Log::getRow, Collectors.toList()));
    logs.clear();
    for (FileRow row : rows) {
      Log log;
      if (map.containsKey(row.getRow())) {
        log = map.get(row.getRow()).get(0);
        log.addCount(Math.toIntExact(row.getCount()));
      } else {
        log = fromFileRowToLog(row);
      }
      logs.add(log);
    }
    saveLog.saveAll(logs);
    deleteFileRow.deleteAll(rows);
  }

  public Log fromFileRowToLog(FileRow fileRow) {
    return new Log(
        fileRow.getRow(),
        Math.toIntExact(fileRow.getCount()),
        fileRow.getFile()
    );
  }
}
