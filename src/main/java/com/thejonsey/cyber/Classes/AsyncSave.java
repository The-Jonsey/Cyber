package com.thejonsey.cyber.Classes;

import com.thejonsey.cyber.Model.*;

import java.sql.Array;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Set;

public class AsyncSave extends Thread {
    private ArrayList<Log> logs;
    private LogRepository logRepository;
    private FilterRepository filterRepository;
    private ArrayList<Filter> columns;

    public AsyncSave(ArrayList<Log> logs, LogRepository logRepository, ArrayList<Filter> filters, FilterRepository filterRepository) {
        this.logs = logs;
        this.logRepository = logRepository;
        this.columns = filters;
        this.filterRepository = filterRepository;
    }

    @Override
    public void run() {
        logRepository.saveAll(logs);
        filterRepository.saveAll(columns);
    }
}
