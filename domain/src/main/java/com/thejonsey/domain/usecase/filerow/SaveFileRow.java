package com.thejonsey.domain.usecase.filerow;

import com.thejonsey.domain.model.FileRow;
import java.util.List;

public interface SaveFileRow {

  void saveAll(List<FileRow> fileRowList);

}
