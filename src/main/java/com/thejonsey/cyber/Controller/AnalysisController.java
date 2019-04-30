package com.thejonsey.cyber.Controller;

import com.thejonsey.cyber.App;
import com.thejonsey.cyber.Model.File;
import com.thejonsey.cyber.Model.FileRepository;
import com.thejonsey.cyber.Model.FilterRepository;
import com.thejonsey.cyber.Model.LogRepository;
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
    public ModelAndView getAnalysis(ModelMap model, @RequestParam Integer file, @RequestParam Integer column) {
        File selectedFile = fileRepository.getById(file);
        if (selectedFile == null) {
            return new IndexController(logRepository, fileRepository, filterRepository).getIndex(model, null, null);
        }
        HashMap<String, Integer> counts = new HashMap<>();
        Boolean[] error = new Boolean[]{false};
        selectedFile.getLogs().forEach(log -> {
            String[] logSplit = log.getRow().split(",");
            if (logSplit.length < column) {
                error[0] = true;
                return;
            }
            if (counts.containsKey(logSplit[column])) {
                counts.put(logSplit[column], counts.get(logSplit[column]) + log.getCount());
            }
            else {
                counts.put(logSplit[column], log.getCount());
            }
        });
        if (error[0]) {
            return new IndexController(logRepository, fileRepository, filterRepository).getIndex(model, null, null);
        }

        LinkedHashMap<String, String> countsJson = new LinkedHashMap<>();
        while (!counts.isEmpty() && countsJson.size() < 20) {
            int index = -1;
            int indexValue = Integer.MAX_VALUE;
            for (int x = 0; x < counts.values().size(); x++) {
                if (Integer.parseInt(counts.values().toArray()[x].toString()) < indexValue) {
                    index = x;
                    indexValue = Integer.parseInt(counts.values().toArray()[x].toString());
                }
            }
            countsJson.put(counts.keySet().toArray()[index].toString(), counts.values().toArray()[index].toString());
            counts.remove(counts.keySet().toArray()[index].toString());
        }
        //counts.forEach((k, v) -> countsJson.put(k, v.toString()));
        model.addAttribute("columnCounts", countsJson);
        model.addAttribute("columnJSON", new JSONObject(countsJson));
        return new ModelAndView("analysis");
    }

}