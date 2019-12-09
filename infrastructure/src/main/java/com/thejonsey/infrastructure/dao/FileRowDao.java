package com.thejonsey.infrastructure.dao;

import com.thejonsey.infrastructure.entity.FileRowEntity;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRowDao extends JpaRepository<FileRowEntity, UUID> {

  Page<FileRowEntity> findAll(Pageable pageable);

}
