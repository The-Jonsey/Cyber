package com.thejonsey.cyber.Controller;

import com.thejonsey.cyber.Classes.AsyncSave;
import com.thejonsey.cyber.Classes.FileModel;
import com.thejonsey.cyber.Classes.HashMapComparator;
import com.thejonsey.cyber.Model.File;
import com.thejonsey.cyber.Model.FileRepository;
import com.thejonsey.cyber.Model.Log;
import com.thejonsey.cyber.Model.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

@Controller
@RequestMapping(path="/")
public class IndexController {

    private final LogRepository logRepository;
    private final FileRepository fileRepository;

    @Autowired
    public IndexController(LogRepository logRepository, FileRepository fileRepository) {
        this.logRepository = logRepository;
        this.fileRepository = fileRepository;
    }

    @Autowired
    ServletContext context;

    @GetMapping(path="/")
    public ModelAndView getIndex(ModelMap model, @RequestParam(required = false) Integer page) {
        return getIndex(model, page, (ArrayList<Log>) logRepository.findAll());
    }

    private ModelAndView getIndex(ModelMap model, Integer page, ArrayList<Log> logs) {
        if (page == null) {
            page = 1;
        }
        if (logs.size() > 0) {
            if (logs.size() > (page * 50)) {
                model.addAttribute("next", true);
            }
            if (page > 1) {
                model.addAttribute("back", true);
            }
            List<Log> logsList = logs.subList(((page - 1) * 50), (page * 50));
            List<HashMap<String, Object>> rows = new ArrayList<>();
            for (Log log : logsList) {
                rows.add(LogToHashMap(log));
            }
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
            new AsyncSave(logs, fileClass, logRepository, fileRepository).start();
            return getIndex(model, null, logs);
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

    @RequestMapping(path="/500")
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void InternalServerError() throws ResponseStatusException{
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Test");
    }
}