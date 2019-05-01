package com.thejonsey.cyber;

import com.thejonsey.cyber.Classes.AsyncSave;
import com.thejonsey.cyber.Model.File;
import com.thejonsey.cyber.Model.FileRepository;
import com.thejonsey.cyber.Model.Filter;
import com.thejonsey.cyber.Model.FilterRepository;
import com.thejonsey.cyber.Model.Log;
import com.thejonsey.cyber.Model.LogRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class DemoApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private FileRepository fileRepository;

	@Autowired
	private FilterRepository filterRepository;

	@Autowired
	private LogRepository logRepository;

	@Test
	public void contextLoads() {
		Assert.assertNotNull("App should have loaded", applicationContext);
	}

	@Test
	public void fileUploadTest() throws Exception {
		runFileTest("kddcup.testdata.csv");
	}
	@Test
	public void bigFileUploadTest() throws Exception {
		runFileTest("kddcup.testdata.unlabeled_10_percent.csv");
	}

	private void runFileTest(String filename) throws Exception {
		try (BufferedReader f = new BufferedReader(new FileReader(filename))) {
			StringBuilder s = new StringBuilder();
			String line = f.readLine();
			while (line != null) {
				s.append(line);
				s.append(System.lineSeparator());
				line = f.readLine();
			}
			LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			List<String> l = new ArrayList<>();
			l.add("true");
			for (int i = 0; i < s.toString().split(System.lineSeparator())[0].split(",").length; i++) {
				params.put(String.valueOf(i), l);
			}
			MockMultipartFile file = new MockMultipartFile("file", filename, "text/csv", s.toString().getBytes());
			MockMvc mock = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
			mock.perform(MockMvcRequestBuilders.multipart("/upload")
					.file(file)
					.params(params))
					.andExpect(status().is(200));
			AsyncSave.instance.join();
			ArrayList<File> files = fileRepository.getAllByFilename(filename);
			junit.framework.Assert.assertTrue(files.size() > 0);
			File file1 = files.get(files.size() - 1);
			System.out.println(file1.getId());
			ArrayList<Filter> filters = filterRepository.findAllByFileid(file1);
			System.out.println(filters.size());
			junit.framework.Assert.assertEquals(filters.size(), params.size());
			ArrayList<Log> logs = logRepository.findAllByFileid(file1);
			System.out.println(logs.size());
			int count = 0;
			for (Log log : logs) {
				count += log.getCount();
			}
			junit.framework.Assert.assertEquals(count, s.toString().split("\n").length);
		}
	}
}
