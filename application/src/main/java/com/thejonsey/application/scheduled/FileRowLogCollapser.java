package com.thejonsey.application.scheduled;

import com.thejonsey.domain.usecase.filerow.CollapseFileRows;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class FileRowLogCollapser {

  private final CollapseFileRows collapseFileRows;

  @Scheduled(fixedDelayString = "PT1M")
  @SchedulerLock(name = "FileRowLogCollapser", lockAtLeastForString = "PT1M")
  void collapse() {
    log.info("FileRowLogCollapser Running");
    collapseFileRows.collapse();
  }
}
