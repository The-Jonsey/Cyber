package com.thejonsey.cyber.Classes;

import com.thejonsey.cyber.Model.*;
import java.util.ArrayList;

/**
 * Saves logs and filters to the database asynchronously
 */
public class AsyncSave extends Thread {
    private ArrayList<Log> logs;
    private LogRepository logRepository;
    private FilterRepository filterRepository;
    private ArrayList<Filter> columns;
    public static AsyncSave instance;

    public AsyncSave(ArrayList<Log> logs, LogRepository logRepository, ArrayList<Filter> filters, FilterRepository filterRepository) {
        this.logs = logs;
        this.logRepository = logRepository;
        this.columns = filters;
        this.filterRepository = filterRepository;
        AsyncSave.instance = this;
    }

    @Override
    public void run() {
        logRepository.saveAll(logs);
        filterRepository.saveAll(columns);
    }
}
