package com.thejonsey.cyber.Controller;

import com.thejonsey.cyber.Classes.AsyncSave;
import com.thejonsey.cyber.Classes.FileModel;
import com.thejonsey.cyber.Classes.HashMapComparator;
import com.thejonsey.cyber.Model.File;
import com.thejonsey.cyber.Model.FileRepository;
import com.thejonsey.cyber.Model.Log;
import com.thejonsey.cyber.Model.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping(path="/")
public class IndexController {

    private final LogRepository logRepository;
    private final FileRepository fileRepository;
    private ArrayList<Log> logs;
    private ArrayList<File> files;
    private HashMap<Integer, ArrayList<Log>> pagedLogs;
    private HashMap<File, HashMap<Integer, ArrayList<Log>>> filedPagedLogs = new HashMap<>();
    @Autowired
    public IndexController(LogRepository logRepository, FileRepository fileRepository) {
        this.logRepository = logRepository;
        this.fileRepository = fileRepository;
    }

    @Autowired
    ServletContext context;

    @GetMapping(path="/")
    private ModelAndView getIndex(ModelMap model, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer file) {
        if (this.logs == null) {
            this.logs = (ArrayList<Log>) logRepository.findAll();
            this.files = (ArrayList<File>) fileRepository.findAll();
            this.pagedLogs = new HashMap<>();
        }
        if (this.pagedLogs.isEmpty()) {
            this.setPagedLogs();
        }
        if (page == null) {
            page = 1;
        }
        if (file == null) {
            file = this.files.get(0).getId();
        }
        File fileObject = null;
        for (File f : this.files) {
            if (f.getId().equals(file)) {
                fileObject = f;
            }
        }
        if (fileObject == null) {
            fileObject = this.files.get(0);
        }
        logs = this.filedPagedLogs.get(fileObject).get(page);
        if (logs.size() > 0) {
            if (this.filedPagedLogs.get(fileObject).size() > page - 1) {
                model.addAttribute("next", true);
            }
            if (page > 1) {
                model.addAttribute("back", true);
            }
            ArrayList<File> fileList = (ArrayList<File>) fileRepository.findAll();
            List<HashMap<String, Object>> files = new ArrayList<>();
            for (File f : fileList) {
                files.add(FileToHashMap(f));
            }
            List<HashMap<String, Object>> rows = new ArrayList<>();
            for (Log log : logs) {
                rows.add(LogToHashMap(log));
            }
            model.addAttribute("files", files);
            model.addAttribute("page", page);
            model.addAttribute("list", rows);
        }
        return new ModelAndView("index");
    }

    @RequestMapping(path = "/", method = RequestMethod.POST)
    public ModelAndView fileUpload(@Validated FileModel file, @RequestParam String seperator, BindingResult result, ModelMap model) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("error", "The file failed to validate");
            return new ModelAndView("index");
        } else {
            MultipartFile multipartFile = file.getFile();
            File fileClass = new File(multipartFile.getOriginalFilename(), new Date(System.currentTimeMillis()));
            String content = new String(multipartFile.getBytes(), StandardCharsets.UTF_8);
            //CSVParser parser = CSVParser.parse(content, ",");
            HashMap<String, Integer> rowsMap = new HashMap<>();
            for (String row : content.split("\\n")) {
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
            this.logs = logs;
            this.files = (ArrayList<File>) fileRepository.findAll();
            this.pagedLogs = new HashMap<>();
            this.setPagedLogs();
            new AsyncSave(logs, fileClass, logRepository, fileRepository).start();
            return getIndex(model, null, null);
        }
    }
    private ArrayList<Object> HashPropertyArray(ArrayList<HashMap<String, Object>> list, String property) {
        ArrayList<Object> properties = new ArrayList<>();
        list.forEach((HashMap<String, Object> item) -> properties.add(item.get(property)));
        return properties;
    }


    private HashMap<String, Object> LogToHashMap(Log log) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("hash", log.getHash());
        map.put("count", log.getCount());
        map.put("row", log.getRow());
        return map;
    }

    private HashMap<String, Object> FileToHashMap(File file) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", file.getId());
        map.put("name", file.getFilename());
        return map;
    }

    private void setPagedLogs() {
        for (int i = 0; i < this.logs.size(); i++) {
            if (i % 50 == 0) {
                ArrayList<Log> sublist = new ArrayList<>(this.logs.subList(i, (i + 50 > this.logs.size() ? this.logs.size() : i + 50)));
                this.pagedLogs.put((i / 50) + 1, sublist);
            }
            File file = this.logs.get(i).getFileid();
            if (!this.filedPagedLogs.containsKey(file)) {
                HashMap<Integer, ArrayList<Log>> map = new HashMap<>();
                map.put(1, new ArrayList<>());
                this.filedPagedLogs.put(file, map);
            }
            Set pages = this.filedPagedLogs.get(file).keySet();
            int page = pages.size();
            if (this.filedPagedLogs.get(file).get(page).size() == 50) {
                this.filedPagedLogs.get(file).put(++page, new ArrayList<>());
            }
            this.filedPagedLogs.get(file).get(page).add(this.logs.get(i));
        }
    }

    @RequestMapping(path="/500")
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void InternalServerError() throws ResponseStatusException{
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Test");
    }
}
