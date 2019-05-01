package com.thejonsey.cyber.Model;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;


public interface LogRepository extends CrudRepository<Log, Integer> {
    public ArrayList<Log> findAllByFileid(File fileid);
}
