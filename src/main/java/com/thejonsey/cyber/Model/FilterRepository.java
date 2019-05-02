package com.thejonsey.cyber.Model;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface FilterRepository extends CrudRepository<Filter, Integer> {
    public ArrayList<Filter> findAllByFileid(File fileid);
}
