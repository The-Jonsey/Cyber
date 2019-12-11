package com.thejonsey.infrastructure.dao;

import com.thejonsey.infrastructure.entity.FileEntity;
import com.thejonsey.infrastructure.entity.LogEntity;
import com.thejonsey.infrastructure.entity.SlimLogEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface LogDao extends JpaRepository<LogEntity, UUID> {

  List<LogEntity> findAllByFile(FileEntity fileId, Pageable pageable);

  Optional<LogEntity> findByRow(String row);

  List<LogEntity> findAllByRowIn(List<String> row);

  @Query(value = "select l.row as row, SUM(l.count) as count "
      + "from LogEntity AS l where l.file in (:files) group by l.row order by SUM(l.count) desc")
  List<SlimLogEntity> findAllGrouped(@Param("files") List<FileEntity> files, Pageable pageable);

}
