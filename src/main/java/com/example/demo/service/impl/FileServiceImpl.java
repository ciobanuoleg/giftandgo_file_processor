package com.example.demo.service.impl;

import java.io.BufferedReader;
import java.io.File;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.List;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.model.Person;
import com.example.demo.service.FileService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implementation class for FileService allowing to process input file into a file required for sending back to user.
 */
@Service
public class FileServiceImpl implements FileService {
	
	public static final int NAME_INDEX = 2;
	public static final int TRANSPORT_INDEX = 4;
	public static final int TOP_SPEED_INDEX = 6;

	@Override
	public Path process(Path filePath) {
		
		Path result = null;
		
		String resultFile = filePath.getParent().toString() + File.separator + System.currentTimeMillis() + "_result.json";
		
		try(BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile())); 
			FileOutputStream resultStream = new FileOutputStream(resultFile) ) {
			
			List<Person> persons = reader
										.lines()
										.map(inputLine -> inputLine.split("\\|"))
										.filter(line -> line.length == 7)
										.map(lineItems-> new Person(lineItems[NAME_INDEX], lineItems[TRANSPORT_INDEX], Double.parseDouble(lineItems[TOP_SPEED_INDEX])))
										.collect(Collectors.toList());
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(resultStream, persons);
			
			result = Path.of(resultFile);
			
		} catch (Throwable  e) {
			throw new RuntimeException(e);
		}
		
		return result;
	}
}
