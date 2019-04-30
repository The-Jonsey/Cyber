package com.thejonsey.cyber.Controller;

import com.thejonsey.cyber.App;
import com.thejonsey.cyber.Classes.AsyncSave;
import com.thejonsey.cyber.Classes.FileModel;
import com.thejonsey.cyber.Classes.HashMapComparator;
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
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(path="/upload")
public class UploadController {
    private final ArrayList<Integer> headers = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39));
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
        model.addAttribute("headers", headers);
        return new ModelAndView("upload");
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView fileUpload(@Validated FileModel file, @RequestParam Map<String,String> allRequestParams, BindingResult result, ModelMap model) throws IOException {
        ArrayList<Integer> selectedHeaders = new ArrayList<>();
        System.out.println(allRequestParams.keySet());
        for (int i = 0; i < allRequestParams.keySet().size(); i++) {
            selectedHeaders.add(i);
        }
        if (result.hasErrors()) {
            model.addAttribute("error", "The file failed to validate");
            return getUpload(model);
        }
        MultipartFile multipartFile = file.getFile();
        File fileClass = new File(multipartFile.getOriginalFilename(), new Date(System.currentTimeMillis()));
        String content = new String(multipartFile.getBytes(), StandardCharsets.UTF_8);
        HashMap<String, Integer> rowsMap = new HashMap<>();
        String[] rowsSplit = content.split("\n");
        for (int x = 0; x < rowsSplit.length; x++) {
            ArrayList<String> rowList = new ArrayList<>(Arrays.asList(rowsSplit[x].split(",")));
            ArrayList<String> rowListNew = new ArrayList<>();
            int size = rowList.size();
            for (int i = 0; i < rowList.size(); i++) {
                if (selectedHeaders.contains(i)) {
                    rowListNew.add(rowList.get(i));
                }
            }
            rowsSplit[x] = String.join(",", rowListNew);
        }
        for (String row : rowsSplit) {
            if (rowsMap.containsKey(row)) {
                rowsMap.put(row, rowsMap.get(row) + 1);
            }
            else {
                rowsMap.put(row, 1);
            }
        }
        ArrayList<HashMap<String, Object>> rowsMapList = new ArrayList<>();
        rowsMap.forEach((k, v) -> {
            try {
                byte[] bytesOfMessage = k.getBytes(StandardCharsets.UTF_8);
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] digest = md.digest(bytesOfMessage);
                BigInteger bigInt = new BigInteger(1, digest);
                String hashtext = bigInt.toString(16);
                HashMap<String, Object> item = new HashMap<>();
                item.put("hash", hashtext);
                item.put("row", k);
                item.put("count", v);
                rowsMapList.add(item);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        });
        rowsMapList.sort(new HashMapComparator());
        ArrayList<Log> logs = (ArrayList<Log>) logRepository.findAll();
        if (logs.size() > 0) {
            ArrayList<String> hashes = new ArrayList<>();
            logs.forEach(log -> hashes.add(log.getHash()));
            rowsMapList.forEach(row -> {
                if (hashes.contains(row.get("hash").toString())) {
                    int index = hashes.indexOf(row.get("hash").toString());
                    Log log = logs.get(index);
                    log.setCount(log.getCount() + (Integer) row.get("count"));
                }
                else {
                    logs.add(new Log(row.get("hash").toString(), row.get("row").toString(), (Integer) row.get("count"), fileClass));
                }
            });
        }
        else {
            rowsMapList.forEach(row -> logs.add(new Log(row.get("hash").toString(), row.get("row").toString(), (Integer) row.get("count"), fileClass)));
        }
        //cache updating
        App.logs = logs;
        fileRepository.save(fileClass);
        App.files.add(fileClass);
        App.pagedLogs = new HashMap<>();
        IndexController.setPagedLogs();
        ArrayList<Filter> filters = new ArrayList<>();
        allRequestParams.keySet().forEach(filter -> {
            filters.add(new Filter(filter, fileClass));
        });
        App.filters.put(fileClass, filters);
        new AsyncSave(logs, logRepository, filters, filterRepository).start();
        return new IndexController(this.logRepository, this.fileRepository, this.filterRepository).getIndex(model, 1, fileClass.getId());
    }

    private ArrayList<Object> HashPropertyArray(ArrayList<HashMap<String, Object>> list, String property) {
        ArrayList<Object> properties = new ArrayList<>();
        list.forEach((HashMap<String, Object> item) -> properties.add(item.get(property)));
        return properties;
    }
}
