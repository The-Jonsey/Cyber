package com.thejonsey.cyber.Controller;

import com.thejonsey.cyber.App;
import com.thejonsey.cyber.Model.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Controller
@RequestMapping(path = "/analyse")
public class AnalysisController {
    private final LogRepository logRepository;
    private final FileRepository fileRepository;
    private final FilterRepository filterRepository;

    @Autowired
    public AnalysisController(LogRepository logRepository, FileRepository fileRepository, FilterRepository filterRepository) {
        this.logRepository = logRepository;
        this.fileRepository = fileRepository;
        this.filterRepository = filterRepository;
    }

    @GetMapping
    public ModelAndView getAnalysis(ModelMap model, @RequestParam(required = false) Integer file, @RequestParam(required = false) Integer column) {
        //region validates get params
        if (file == null || column == null) {
            return new IndexController(logRepository, fileRepository, filterRepository).getIndex(model, null, null);
        }
        File selectedFile = fileRepository.getById(file);
        if (selectedFile == null) {
            return new IndexController(logRepository, fileRepository, filterRepository).getIndex(model, null, null);
        }
        ArrayList<Filter> filters = filterRepository.findAllByFileid(selectedFile);
        if (filters.size() < column) {
            return new IndexController(logRepository, fileRepository, filterRepository).getIndex(model, null, null);
        }
        //endregion
        HashMap<String, Integer> counts = new HashMap<>(); //hashmap of column v count
        selectedFile.getLogs().forEach(log -> {
            String[] logSplit = log.getRow().split(",");
            //region adds column value and count to hashmap, if value already exists, add this count to existing count
            if (counts.containsKey(logSplit[column])) {
                counts.put(logSplit[column], counts.get(logSplit[column]) + log.getCount());
            }
            else {
                counts.put(logSplit[column], log.getCount());
            }
            //endregion
        });

        LinkedHashMap<String, String> countsJson = new LinkedHashMap<>();
        while (!counts.isEmpty() && countsJson.size() < App.analysis_amount) { //generates list of x least common rows, where x is defined in config
            //region finds lowest occuring row
            int index = -1;
            int indexValue = Integer.MAX_VALUE;
            for (int x = 0; x < counts.values().size(); x++) {
                if (Integer.parseInt(counts.values().toArray()[x].toString()) < indexValue) {
                    index = x;
                    indexValue = Integer.parseInt(counts.values().toArray()[x].toString());
                }
            }
            //endregion
            //region adds row to map
            countsJson.put(counts.keySet().toArray()[index].toString(), counts.values().toArray()[index].toString());
            counts.remove(counts.keySet().toArray()[index].toString());
            //endregion
        }
        //region attributes for rendering engine
        model.addAttribute("title", App.title);
        model.addAttribute("columnCounts", countsJson);
        model.addAttribute("columnJSON", new JSONObject(countsJson));
        //endregion
        return new ModelAndView("analysis");
    }

}
