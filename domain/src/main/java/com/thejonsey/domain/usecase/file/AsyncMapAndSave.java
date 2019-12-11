package com.thejonsey.domain.usecase.file;

import com.thejonsey.domain.model.File;
import com.thejonsey.domain.model.Log;
import com.thejonsey.domain.usecase.log.SaveLog;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AsyncMapAndSave implements Runnable {

  private List<String> content;
  private File file;
  private final SaveLog saveLog;
  private final SaveFile saveFile;

  private void save() {
    Map<String, Long> map = content.stream()
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    content = new ArrayList<>(new HashSet<>(content));
    saveLog.saveAll(content.stream().map(row ->
        new Log(
            row,
            map.get(row).intValue(),
            file
        )
    ).collect(Collectors.toList()));
    file.complete();
    saveFile.save(file);
  }

  public void run() {
    save();
  }


}
