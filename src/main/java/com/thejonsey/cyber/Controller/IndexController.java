package com.thejonsey.cyber.Controller;

import com.thejonsey.cyber.Model.*;
import com.thejonsey.cyber.App;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.ServletContext;
import java.util.*;

@Controller
@RequestMapping(path = "/")
public class IndexController {
    private final LogRepository logRepository;
    private final FileRepository fileRepository;
    private final FilterRepository filterRepository;

    @Autowired
    public IndexController(LogRepository logRepository, FileRepository fileRepository, FilterRepository filterRepository) {
        this.logRepository = logRepository;
        this.fileRepository = fileRepository;
        this.filterRepository = filterRepository;
    }

    @Autowired
    ServletContext context;

    @GetMapping()
    ModelAndView getIndex(ModelMap model, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer file) {
        // region cache access
        boolean itemchanged = false;
        if (App.logs.isEmpty()) {
            App.logs = (ArrayList<Log>) logRepository.findAll();
            itemchanged = true;
        }
        if (App.files.isEmpty()) {
            App.files = (ArrayList<File>) fileRepository.findAll();
            itemchanged = true;
        }
        if (App.pagedLogs.isEmpty() || itemchanged) {
            setPagedLogs();
        }
        if (App.filters.isEmpty()) {
            App.files.forEach(f -> {
                App.filters.put(f, filterRepository.findAllByFileid(f));
            });
        }
        if (page == null) {
            page = 1;
        }
        if (App.files.size() == 0) {
            return new ModelAndView("index");
        }
        if (file == null ) {
            file = App.files.get(0).getId();
        }
        File fileObject = null;
        for (File f : App.files) {
            if (f.getId().equals(file)) {
                fileObject = f;
            }
        }
        if (fileObject == null) {
            fileObject = App.files.get(0);
        }
        ArrayList<Log> logs = App.filedPagedLogs.get(fileObject).get(page);
        // endregion
        if (logs.size() > 0) {
            if (App.filedPagedLogs.get(fileObject).size() > page) {
                model.addAttribute("next", true);
            }
            if (page > 1) {
                model.addAttribute("back", true);
            }
            ArrayList<File> fileList = (ArrayList<File>) fileRepository.findAll();
            ArrayList<Filter> filters = App.filters.get(fileObject);
            String[] col = new String[filters.size()];
            for (int i = 0; i < filters.size(); i++) {
                col[i] = filters.get(i).getFilter();
            }
            List<HashMap<String, Object>> files = new ArrayList<>();
            for (File f : fileList) {
                files.add(FileToHashMap(f, col));
            }
            List<HashMap<String, Object>> rows = new ArrayList<>();
            for (Log log : logs) {
                rows.add(LogToHashMap(log, col));
            }
            ArrayList<String> cols = new ArrayList<>(Arrays.asList(col));
            cols.add(0, "Count");
            model.addAttribute("columns", cols);
            model.addAttribute("files", files);
            model.addAttribute("file", fileObject.getId());
            model.addAttribute("page", page);
            model.addAttribute("list", rows);
        }
        return new ModelAndView("index");
    }


    private HashMap<String, Object> LogToHashMap(Log log, String[] columns) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("Count", log.getCount());
        String[] logSplit = log.getRow().split(",");
        for (int i = 0; i < logSplit.length; i++) {
            map.put(columns[i], logSplit[i]);
        }
        return map;
    }

    private HashMap<String, Object> FileToHashMap(File file, String[] col) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", file.getId());
        map.put("name", file.getFilename());
        map.put("date", file.getUploaded());
        map.put("columns", col);
        return map;
    }

    public static void setPagedLogs() {
        for (File file : App.files) {
            if (!App.filedPagedLogs.containsKey(file)) {
                HashMap<Integer, ArrayList<Log>> map = new HashMap<>();
                map.put(1, new ArrayList<>());
                App.filedPagedLogs.put(file, map);
            }
        }
        for (int i = 0; i < App.logs.size(); i++) {
            if (i % 50 == 0) {
                ArrayList<Log> sublist = new ArrayList<>(App.logs.subList(i, (i + 50 > App.logs.size() ? App.logs.size() : i + 50)));
                App.pagedLogs.put((i / 50) + 1, sublist);
            }
            File file = App.logs.get(i).getFileid();
            for (File file1 : App.files) {
                if (file1.getId().equals(file.getId())) {
                    file = file1;
                    break;
                }
            }
            Set pages = App.filedPagedLogs.get(file).keySet();
            int page = pages.size();
            if (App.filedPagedLogs.get(file).get(page).size() == 50) {
                App.filedPagedLogs.get(file).put(++page, new ArrayList<>());
            }
            App.filedPagedLogs.get(file).get(page).add(App.logs.get(i));
        }
    }

    @RequestMapping(path="/500")
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void InternalServerError() throws ResponseStatusException{
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Test");
    }
}
