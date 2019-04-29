package com.thejonsey.cyber.Classes;

import com.thejonsey.cyber.Model.File;
import com.thejonsey.cyber.Model.FileRepository;
import com.thejonsey.cyber.Model.Log;
import com.thejonsey.cyber.Model.LogRepository;

import java.sql.Statement;
import java.util.ArrayList;

public class AsyncSave extends Thread {
    private ArrayList<Log> logs;
    private LogRepository logRepository;

    public AsyncSave(ArrayList<Log> logs, LogRepository logRepository) {
        this.logs = logs;
        this.logRepository = logRepository;
    }

    @Override
    public void run() {
        logRepository.saveAll(logs);
    }
}
