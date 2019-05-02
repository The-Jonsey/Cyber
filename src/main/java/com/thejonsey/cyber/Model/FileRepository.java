package com.thejonsey.cyber.Model;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;


public interface FileRepository extends CrudRepository<File, Integer> {
    public File getById(Integer id);

    public ArrayList<File> getAllByFilename(String filename);
}
