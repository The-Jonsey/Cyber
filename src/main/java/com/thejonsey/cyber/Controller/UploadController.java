package com.thejonsey.cyber.Controller;

import com.thejonsey.cyber.App;
import com.thejonsey.cyber.Classes.AsyncSave;
import com.thejonsey.cyber.Classes.FileModel;
import com.thejonsey.cyber.Classes.HashMapComparator;
import com.thejonsey.cyber.Model.File;
import com.thejonsey.cyber.Model.FileRepository;
import com.thejonsey.cyber.Model.Log;
import com.thejonsey.cyber.Model.LogRepository;
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
public class UploadController {
    private final ArrayList<String> headers = new ArrayList<>(Arrays.asList("duration","protocol_type","service","flag","src_bytes","dst_bytes","land","wrong_fragment","urgent","hot","num_failed_logins","logged_in","num_compromised","root_shell","su_attempted","num_root","num_file_creations","num_shells","num_access_files","num_outbound_cmds","is_host_login","is_guest_login","count","srv_count","serror_rate","srv_serror_rate","same_srv_rate","diff_srv_rate","srv_diff_host_rate","dst_host_count","dst_host_srv_count","dst_host_same_srv_rate","dst_host_diff_srv_rate","dst_host_same_src_port_rate","dst_host_srv_diff_host_rate","dst_host_serror_rate","dst_host_srv_serror_rate","dst_host_rerror_rate","dst_host_srv_rerror_rate"));
    private LogRepository logRepository;
    private FileRepository fileRepository;

    @Autowired
    public UploadController(LogRepository logRepository, FileRepository fileRepository) {
        this.logRepository = logRepository;
        this.fileRepository = fileRepository;
    }

    @GetMapping(path="/upload")
    public ModelAndView getUpload(ModelMap model) {
        model.addAttribute("headers", headers);
        return new ModelAndView("upload");
    }

    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public ModelAndView fileUpload(@Validated FileModel file, @RequestParam Map<String,String> allRequestParams, BindingResult result, ModelMap model) throws IOException {
        ArrayList<Integer> selectedHeaders = new ArrayList<>();
        System.out.println(allRequestParams.keySet());
        for (int i = 0; i < headers.size(); i++) {
            if (!allRequestParams.containsKey(headers.get(i))) {
                selectedHeaders.add(i);
            }
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
            for (int i = rowList.size() - 1; i > 0; i--) {
                if (!selectedHeaders.contains(i)) {
                    rowList.remove(i);
                }
            }
            rowsSplit[x] = String.join(",", rowList);
        }
        for (String row : content.split("\n")) {
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
        new AsyncSave(logs, logRepository).start();
        return new IndexController(this.logRepository, this.fileRepository).getIndex(model, 1, fileClass.getId());
    }

    private ArrayList<Object> HashPropertyArray(ArrayList<HashMap<String, Object>> list, String property) {
        ArrayList<Object> properties = new ArrayList<>();
        list.forEach((HashMap<String, Object> item) -> properties.add(item.get(property)));
        return properties;
    }
}
