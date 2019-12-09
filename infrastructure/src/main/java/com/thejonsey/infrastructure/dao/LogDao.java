package com.thejonsey.infrastructure.dao;

import com.thejonsey.infrastructure.entity.FileEntity;
import com.thejonsey.infrastructure.entity.LogEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LogDao extends JpaRepository<LogEntity, UUID> {

  List<LogEntity> findAllByFile(FileEntity fileId);

  Optional<LogEntity> findByRow(String row);

  List<LogEntity> findAllByRowIn(List<String> row);
}
