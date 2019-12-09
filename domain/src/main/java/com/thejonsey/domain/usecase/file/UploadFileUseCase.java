package com.thejonsey.domain.usecase.file;

import com.thejonsey.domain.model.File;
import com.thejonsey.domain.model.FileRow;
import com.thejonsey.domain.usecase.filerow.SaveFileRow;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UploadFileUseCase implements UploadFile {

  private final SaveFile saveFile;
  private final SaveFileRow saveFileRow;

  @Override
  public File upload(List<String> content, String fileName) {
    File file = new File(fileName, content.size());
    saveFile.save(file);
    Map<String, Long> map = content.stream()
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    content = new ArrayList<>(new HashSet<>(content));
    saveFileRow.saveAll(content.stream().map(row ->
        new FileRow(
            file,
            row,
            map.get(row)
        )
    ).collect(Collectors.toList()));
    return file;
  }
}
