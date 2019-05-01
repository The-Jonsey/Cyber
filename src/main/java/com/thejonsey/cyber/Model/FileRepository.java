package com.thejonsey.cyber.Model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;


public interface FileRepository extends CrudRepository<File, Integer> {
    public File getById(Integer id);

    public ArrayList<File> getAllByFilename(String filename);
}
