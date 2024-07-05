package com.example.demo.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.model.Person;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Successful test for processing of an input file by FIleService.
 */

@ActiveProfiles(value = "integration")
@SpringBootTest
public class FileServiceTest {

	private static final String ENTRY_FILE = "EntryFile.txt";
	
	@Autowired
	FileService service;
	
	@Test
	public void testProcessFile() throws IOException, URISyntaxException{
		URL url = this.getClass().getClassLoader().getResource(ENTRY_FILE);
        Path path = Paths.get(url.toURI());
        
		Path result = service.process(path);
		
		List<String> content = Files.readAllLines(result);
		String onePlaceContent = content.stream().collect(Collectors.joining());
		ObjectMapper mapper = new ObjectMapper();
		Person[] persons = mapper.readValue(onePlaceContent, Person[].class);
		// check number of persons
		Assertions.assertEquals(3, persons.length);
		// check first person
		Assertions.assertEquals("John Smith", persons[0].name());
		Assertions.assertEquals("Rides A Bike", persons[0].transport());
		Assertions.assertEquals(12.1, persons[0].topSpeed());
		
		result.toFile().delete();
	}
}
