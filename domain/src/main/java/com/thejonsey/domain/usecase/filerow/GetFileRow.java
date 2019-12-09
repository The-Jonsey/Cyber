package com.thejonsey.domain.usecase.filerow;

import com.thejonsey.domain.model.FileRow;
import java.util.List;

public interface GetFileRow {

  List<FileRow> findAllLimit(int limit);

}
