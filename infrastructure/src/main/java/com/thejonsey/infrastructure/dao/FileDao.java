package com.thejonsey.infrastructure.dao;

import com.thejonsey.infrastructure.entity.FileEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FileDao extends JpaRepository<FileEntity, UUID> {

  Optional<FileEntity> findById(UUID id);

  List<FileEntity> findAllByFilename(String filename);
}
