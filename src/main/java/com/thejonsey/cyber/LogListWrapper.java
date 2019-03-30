package com.thejonsey.cyber;

import java.util.ArrayList;

public class LogListWrapper {

    public LogListWrapper(ArrayList<Log> logs) {
        this.logs = logs;
    }

    private ArrayList<Log> logs;

    public ArrayList<Log> getLogs() {
        return logs;
    }

    public void setLogs(ArrayList<Log> logs) {
        this.logs = logs;
    }
}
