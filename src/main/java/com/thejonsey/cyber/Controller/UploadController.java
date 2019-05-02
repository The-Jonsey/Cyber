package com.thejonsey.cyber.Controller;

import com.thejonsey.cyber.App;
import com.thejonsey.cyber.Classes.AsyncSave;
import com.thejonsey.cyber.Classes.FileModel;
import com.thejonsey.cyber.Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.*;

@Controller
@RequestMapping(path="/upload")
public class UploadController {
    private LogRepository logRepository;
    private FileRepository fileRepository;
    private FilterRepository filterRepository;

    @Autowired
    public UploadController(LogRepository logRepository, FileRepository fileRepository, FilterRepository filterRepository) {
        this.logRepository = logRepository;
        this.fileRepository = fileRepository;
        this.filterRepository = filterRepository;
    }

    @GetMapping()
    public ModelAndView getUpload(ModelMap model) {
        model.addAttribute("title", App.title);
        return new ModelAndView("upload");
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView fileUpload(@Validated FileModel file, @RequestParam Map<String,String> allRequestParams, BindingResult result, ModelMap model) throws IOException {
        //region generates a list of selected headers from the HTML form
        ArrayList<Integer> selectedHeaders = new ArrayList<>();
        for (int i = 0; i < allRequestParams.keySet().size(); i++) {
            selectedHeaders.add(Integer.parseInt(allRequestParams.keySet().toArray()[i].toString()));
        }
        //endregion
        //region validates and loads the file into memory
        if (result.hasErrors()) {
            model.addAttribute("error", "The file failed to validate");
            return getUpload(model);
        }
        MultipartFile multipartFile = file.getFile();
        String[] nameSplit = multipartFile.getOriginalFilename().split("\\.");
        if (!nameSplit[nameSplit.length - 1].equals("csv")) {
            model.addAttribute("error", "That is not a CSV");
            return getUpload(model);
        }
        File fileClass = new File(multipartFile.getOriginalFilename(), new Date(System.currentTimeMillis()));
        String content = new String(multipartFile.getBytes(), StandardCharsets.UTF_8);
        //endregion
        LinkedHashMap<String, Integer> rowsMap = new LinkedHashMap<>();
        content = content.replaceAll("\r", ""); //removes carrage return, just in case file is DOS encoded
        String[] rowsSplit = content.split("\n");//splits csv on line break
        for (String s : rowsSplit) {
            //region filters out unselected headers from HTML form
            ArrayList<String> rowList = new ArrayList<>(Arrays.asList(s.split(",")));
            ArrayList<String> rowListNew = new ArrayList<>();
            for (int i = 0; i < rowList.size(); i++) {
                if (selectedHeaders.contains(i)) {
                    rowListNew.add(rowList.get(i));
                }
            }
            //endregion
            //region adds row to map of row v count
            String row = String.join(",", rowListNew);
            if (rowsMap.containsKey(row)) {
                rowsMap.put(row, rowsMap.get(row) + 1);
            } else {
                rowsMap.put(row, 1);
            }
            //endregion
        }
        //region puts hashmap into list of logs, and sorts logs by frequency of row in descending order
        ArrayList<Log> logs = new ArrayList<>();
        rowsMap.forEach((k, v) -> logs.add(new Log(k, v, fileClass)));
        logs.sort((log, t1) -> log.getCount() > t1.getCount() ? -1 : log.getCount().equals(t1.getCount()) ? 0 : 1);
        //endregion
        //region saving to cache and database
        if (App.logs.isEmpty()) {
            App.logs = logs;
        }
        else {
            App.logs.addAll(logs);
        }
        fileRepository.save(fileClass);
        App.files.add(fileClass);
        App.pagedLogs = new HashMap<>();
        App.setPagedLogs();
        ArrayList<Filter> filters = new ArrayList<>();
        allRequestParams.keySet().forEach(filter -> filters.add(new Filter(filter, fileClass)));
        App.filters.put(fileClass, filters);
        new AsyncSave(logs, logRepository, filters, filterRepository).start();
        //endregion
        return new IndexController(this.logRepository, this.fileRepository, this.filterRepository).getIndex(model, 1, fileClass.getId()); //redirects to index page
    }
}
