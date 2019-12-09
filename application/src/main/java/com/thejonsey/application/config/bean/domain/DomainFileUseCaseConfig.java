package com.thejonsey.application.config.bean.domain;

import com.thejonsey.domain.usecase.file.GetFile;
import com.thejonsey.domain.usecase.file.SaveFile;
import com.thejonsey.domain.usecase.file.UploadFile;
import com.thejonsey.domain.usecase.file.UploadFileUseCase;
import com.thejonsey.domain.usecase.filerow.SaveFileRow;
import com.thejonsey.infrastructure.dao.FileDao;
import com.thejonsey.infrastructure.repository.FileRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainFileUseCaseConfig {

  @Bean
  public UploadFile uploadFile(SaveFile saveFile, SaveFileRow saveFileRow) {
    return new UploadFileUseCase(saveFile, saveFileRow);
  }

  @Bean
  public SaveFile saveFile(FileDao fileDao) {
    return new FileRepository(fileDao);
  }

  @Bean
  public GetFile getFile(FileDao fileDao) {
    return new FileRepository(fileDao);
  }

}
