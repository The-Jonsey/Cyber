package com.thejonsey.application.config.bean.domain;

import com.thejonsey.domain.usecase.filerow.CollapseFileRows;
import com.thejonsey.domain.usecase.filerow.CollapseFileRowsUseCase;
import com.thejonsey.domain.usecase.filerow.DeleteFileRow;
import com.thejonsey.domain.usecase.filerow.GetFileRow;
import com.thejonsey.domain.usecase.filerow.SaveFileRow;
import com.thejonsey.domain.usecase.log.GetLog;
import com.thejonsey.domain.usecase.log.SaveLog;
import com.thejonsey.infrastructure.dao.FileRowDao;
import com.thejonsey.infrastructure.repository.FileRowRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainFileRowUseCaseConfig {

  @Bean
  public SaveFileRow saveFileRow(FileRowDao fileRowDao) {
    return new FileRowRepository(fileRowDao);
  }

  @Bean
  public GetFileRow getFileRow(FileRowDao fileRowDao) {
    return new FileRowRepository(fileRowDao);
  }

  @Bean
  public DeleteFileRow deleteFileRow(FileRowDao fileRowDao) {
    return new FileRowRepository(fileRowDao);
  }

  @Bean
  public CollapseFileRows collapseFileRows(GetFileRow getFileRow, DeleteFileRow deleteFileRow,
      GetLog getLog, SaveLog saveLog) {
    return new CollapseFileRowsUseCase(getFileRow, deleteFileRow, getLog, saveLog);
  }

}
