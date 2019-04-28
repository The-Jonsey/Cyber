package com.thejonsey.cyber.Model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface FileRepository extends CrudRepository<File, Integer> {

}
