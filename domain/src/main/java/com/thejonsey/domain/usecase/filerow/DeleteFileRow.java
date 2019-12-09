package com.thejonsey.domain.usecase.filerow;

import com.thejonsey.domain.model.FileRow;
import java.util.List;

public interface DeleteFileRow {

  void delete(FileRow fileRow);

  void deleteAll(List<FileRow> fileRows);

}
