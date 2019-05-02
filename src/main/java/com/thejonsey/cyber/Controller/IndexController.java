package com.thejonsey.cyber.Controller;

import com.thejonsey.cyber.Model.*;
import com.thejonsey.cyber.App;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
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
        model.addAttribute("title", App.title);
        // region validate and regenerate cache
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
            App.setPagedLogs();
        }
        if (App.filters.isEmpty()) {
            App.files.forEach(f -> {
                App.filters.put(f, filterRepository.findAllByFileid(f));
            });
        }
        //endregion
        //region default page and file if not specified in get params
        if (page == null) {
            page = 1;
        }
        if (App.files.size() == 0) {
            return new ModelAndView("index");
        }
        if (file == null ) {
            file = App.files.get(0).getId();
        }
        //endregion
        //region get file from fileid
        File fileObject = null;
        for (File f : App.files) {
            if (f.getId().equals(file)) {
                fileObject = f;
            }
        }
        if (fileObject == null) {
            fileObject = App.files.get(0);
        }
        //endregion
        ArrayList<Log> logs = App.filedPagedLogs.get(fileObject).get(page); //get relevent logs from file an page specified
        if (logs.size() > 0) {
            //region adds next page and previous page buttons where applicable
            if (App.filedPagedLogs.get(fileObject).size() > page) {
                model.addAttribute("next", true);
            }
            if (page > 1) {
                model.addAttribute("back", true);
            }
            //endregion
            //region gets the filters from the database, as specified in the upload form
            ArrayList<File> fileList = (ArrayList<File>) fileRepository.findAll();
            ArrayList<Filter> filters = App.filters.get(fileObject);
            String[] col = new String[filters.size()];
            for (int i = 0; i < filters.size(); i++) {
                col[i] = filters.get(i).getFilter();
            }
            //endregion
            //region converts files and logs into hashmaps
            List<HashMap<String, Object>> files = new ArrayList<>();
            for (File f : fileList) {
                files.add(FileToHashMap(f, col));
            }
            List<HashMap<String, Object>> rows = new ArrayList<>();
            for (Log log : logs) {
                rows.add(LogToHashMap(log, col));
            }
            //endregion
            ArrayList<String> cols = new ArrayList<>(Arrays.asList(col));
            cols.add(0, "Count");
            //region attributes for rendering engine
            model.addAttribute("columns", cols);
            model.addAttribute("files", files);
            model.addAttribute("file", fileObject.getId());
            model.addAttribute("page", page);
            model.addAttribute("list", rows);
            //endregion
        }
        return new ModelAndView("index");
    }

    /**
     *
     * @param log - Instance of log class, 1 row from database
     * @param columns - columns for the rendering engine
     * @return hashmap of log
     */
    private HashMap<String, Object> LogToHashMap(Log log, String[] columns) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("Count", log.getCount());
        String[] logSplit = log.getRow().split(",");
        for (int i = 0; i < logSplit.length; i++) {
            map.put(columns[i], logSplit[i]);
        }
        return map;
    }

    /**
     *
     * @param file - Instance of file class, 1 file from database
     * @param col - columns for rendering engine
     * @return hashmap of file
     */
    private HashMap<String, Object> FileToHashMap(File file, String[] col) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", file.getId());
        map.put("name", file.getFilename());
        map.put("date", file.getUploaded());
        map.put("columns", col);
        return map;
    }
}
