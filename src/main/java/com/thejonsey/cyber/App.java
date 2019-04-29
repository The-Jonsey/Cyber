package com.thejonsey.cyber;

import com.thejonsey.cyber.Model.File;
import com.thejonsey.cyber.Model.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.util.ArrayList;
import java.util.HashMap;

@SpringBootApplication
public class App extends SpringBootServletInitializer {
	// cach variables
	public static ArrayList<Log> logs = new ArrayList<>();
	public static ArrayList<File> files = new ArrayList<>();
	public static HashMap<Integer, ArrayList<Log>> pagedLogs = new HashMap<>();
	public static HashMap<File, HashMap<Integer, ArrayList<Log>>> filedPagedLogs = new HashMap<>();

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

}
