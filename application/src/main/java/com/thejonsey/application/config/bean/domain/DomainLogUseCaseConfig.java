package com.thejonsey.application.config.bean.domain;

import com.thejonsey.domain.usecase.log.GetLog;
import com.thejonsey.domain.usecase.log.SaveLog;
import com.thejonsey.infrastructure.dao.LogDao;
import com.thejonsey.infrastructure.repository.LogRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainLogUseCaseConfig {

  @Bean
  public GetLog getLog(LogDao logDao) {
    return new LogRepository(logDao);
  }

  @Bean
  public SaveLog saveLog(LogDao logDao) {
    return new LogRepository(logDao);
  }
}
