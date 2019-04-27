package com.thejonsey.cyber.Classes;

import com.thejonsey.cyber.Model.File;
import com.thejonsey.cyber.Model.FileRepository;
import com.thejonsey.cyber.Model.Log;
import com.thejonsey.cyber.Model.LogRepository;

import java.sql.Statement;
import java.util.ArrayList;

public class AsyncSave extends Thread {
    private ArrayList<Log> logs;
    private File file;
    private LogRepository logRepository;
    private FileRepository fileRepository;

    public AsyncSave(ArrayList<Log> logs, File file, LogRepository logRepository, FileRepository fileRepository) {
        this.logs = logs;
        this.file = file;
        this.logRepository = logRepository;
        this.fileRepository = fileRepository;
    }

    @Override
    public void run() {
        fileRepository.save(file);
        logRepository.saveAll(logs);
    }
}
