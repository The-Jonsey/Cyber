package com.thejonsey.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories("com.thejonsey")
@EntityScan("com.thejonsey")
@ComponentScan("com.thejonsey")
@Slf4j
public class App {

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
    log.debug("DEBUG MODE ENABLED");
  }
}
