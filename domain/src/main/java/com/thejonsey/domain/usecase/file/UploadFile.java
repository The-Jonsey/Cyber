package com.thejonsey.domain.usecase.file;

import com.thejonsey.domain.model.File;
import java.util.List;

public interface UploadFile {

  File upload(List<String> content, String fileName);

}
