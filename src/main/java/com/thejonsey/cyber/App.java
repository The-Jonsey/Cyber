package com.thejonsey.cyber;

import com.thejonsey.cyber.Classes.AppProperties;
import com.thejonsey.cyber.Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

@SpringBootApplication
public class App extends SpringBootServletInitializer {
	public static void setPagedLogs() {
		//region Adds files to the hashmap of files v pages of logs
		for (File file : App.files) {
			if (!App.filedPagedLogs.containsKey(file)) {
				HashMap<Integer, ArrayList<Log>> map = new HashMap<>();
				map.put(1, new ArrayList<>());
				App.filedPagedLogs.put(file, map);
			}
		}
		//endregion
		for (int i = 0; i < App.logs.size(); i++) {
			//region Adds logs to pages of logs
			if (i % page_size == 0) {
				ArrayList<Log> sublist = new ArrayList<>(App.logs.subList(i, (i + page_size > App.logs.size() ? App.logs.size() : i + page_size)));
				App.pagedLogs.put((i / page_size) + 1, sublist);
			}
			//endregion
			//region links log to file
			File file = App.logs.get(i).getFileid();
			for (File file1 : App.files) {
				if (file1.getId().equals(file.getId())) {
					file = file1;
					break;
				}
			}
			//endregion
			//region Places log in hashmap of files v pages of log
			Set pages = App.filedPagedLogs.get(file).keySet();
			int page = pages.size();
			if (App.filedPagedLogs.get(file).get(page).size() == page_size) {
				App.filedPagedLogs.get(file).put(++page, new ArrayList<>());
			}
			App.filedPagedLogs.get(file).get(page).add(App.logs.get(i));
			//endregion
		}
	}
	//region config variables
	private static Integer page_size;
	public static Integer analysis_amount;
	//endregion
	//region cache variables
	public static ArrayList<Log> logs = new ArrayList<>();
	public static ArrayList<File> files = new ArrayList<>();
	public static HashMap<Integer, ArrayList<Log>> pagedLogs = new HashMap<>();
	public static HashMap<File, HashMap<Integer, ArrayList<Log>>> filedPagedLogs = new HashMap<>();
	public static HashMap<File, ArrayList<Filter>> filters = new HashMap<>();
	//endregion
	@Autowired
	public App(FilterRepository filterRepository, FileRepository fileRepository, LogRepository logRepository, AppProperties appProperties) {
		App.page_size = appProperties.getPage_size();
		App.analysis_amount = appProperties.getAnalysis_amount();
		//region Generates cache
		logs = (ArrayList<Log>) logRepository.findAll();
		files = (ArrayList<File>) fileRepository.findAll();
		App.files.forEach(f -> App.filters.put(f, filterRepository.findAllByFileid(f)));
		setPagedLogs();
		//endregion
	}

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

}
