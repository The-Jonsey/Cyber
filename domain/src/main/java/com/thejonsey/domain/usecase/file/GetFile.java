package com.thejonsey.domain.usecase.file;

import com.thejonsey.domain.model.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GetFile {

  Optional<File> byId(UUID id);

  List<File> byFilename(String filename);

}
