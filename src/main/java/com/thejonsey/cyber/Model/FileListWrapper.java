package com.thejonsey.cyber.Model;

import java.util.ArrayList;

public class FileListWrapper {

    public FileListWrapper(ArrayList<File> files) {
        this.files = files;
    }

    private ArrayList<File> files;

    public ArrayList<File> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<File> files) {
        this.files = files;
    }
}
