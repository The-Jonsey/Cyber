package com.thejonsey.cyber.Model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;

public interface FilterRepository extends CrudRepository<Filter, Integer> {
    ArrayList<Filter> findAllByFileid(File fileid);
}
