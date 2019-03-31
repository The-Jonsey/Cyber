package com.thejonsey.cyber;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping(path="/")
public class IndexController {

    private final LogRepository logRepository;

    @Autowired
    public IndexController(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Autowired
    ServletContext context;

    @GetMapping(path="/")
    public ModelAndView getIndex(ModelMap model) {
        ArrayList<Log> logs = (ArrayList<Log>) logRepository.findAll();
        List<HashMap<String, Object>> rows = new ArrayList<>();
        logs.forEach(log -> rows.add(LogToHashMap(log)));
        System.out.println(rows);
        if (rows.size() > 0)
        model.addAttribute("list", rows);
        return new ModelAndView("index");
    }

    @RequestMapping(path = "/", method = RequestMethod.POST)
    public ModelAndView fileUpload(@Validated FileModel file, @RequestParam String seperator, BindingResult result, ModelMap model) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("error", "The file failed to validate");
            return new ModelAndView("index");
        } else {
            MultipartFile multipartFile = file.getFile();
            String content = new String(multipartFile.getBytes(), StandardCharsets.UTF_8);
            ArrayList<HashMap<String, Object>> rowsMap = new ArrayList<>();
            for (String row : content.split("\\n")) {
                try {
                    byte[] bytesOfMessage = row.getBytes(StandardCharsets.UTF_8);
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    byte[] digest = md.digest(bytesOfMessage);
                    BigInteger bigInt = new BigInteger(1, digest);
                    String hashtext = bigInt.toString(16);

                    final Boolean[] replaced = {false};
                    rowsMap.forEach((HashMap<String, Object> item) -> {
                        if (item.get("hash").toString().equals(hashtext)) {
                            item.replace("count", (Integer) item.get("count") + 1);
                            replaced[0] = true;
                        }
                    });
                    if (!replaced[0]) {
                        HashMap<String, Object> item = new HashMap<>();
                        item.put("hash", hashtext);
                        item.put("row", row);
                        item.put("count", 1);
                        rowsMap.add(item);
                    }
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
            rowsMap.sort(new HashMapComparator());
            System.out.println(HashPropertyArray(rowsMap, "count"));
            ArrayList<Log> logs = (ArrayList<Log>) logRepository.findAll();
            rowsMap.forEach(row -> {
                Boolean[] set = {false};
                logs.forEach(log -> {
                    if (log.getHash().equals(row.get("hash").toString())) {
                        log.setCount(log.getCount() + (Integer) row.get("count"));
                        set[0] = true;
                    }
                });
                if (!set[0]) {
                    logs.add(new Log(row.get("hash").toString(), row.get("row").toString(), (Integer) row.get("count")));
                }
            });
            logRepository.saveAll(logs);
            return getIndex(model);
        }
    }

    private ArrayList<Object> HashPropertyArray(ArrayList<HashMap<String, Object>> list, String property) {
        ArrayList<Object> properties = new ArrayList<>();
        list.forEach((HashMap<String, Object> item) -> {
            properties.add(item.get(property));
        });
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
