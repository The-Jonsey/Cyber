package com.thejonsey.domain.usecase.file;

import com.thejonsey.domain.model.File;
import com.thejonsey.domain.usecase.log.SaveLog;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UploadFileUseCase implements UploadFile {

  private final SaveFile saveFile;
  private final SaveLog saveLog;

  @Override
  public File upload(List<String> content, String fileName) {
    File file = new File(fileName, content.size());
    saveFile.save(file);
    Thread t = new Thread(new AsyncMapAndSave(content, file, saveLog, saveFile));
    t.start();
    return file;
  }
}
