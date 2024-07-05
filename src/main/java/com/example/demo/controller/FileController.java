package com.example.demo.controller;

import java.io.IOException;

import java.nio.file.Path;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.io.CleaningFileInputStream;
import com.example.demo.model.FileProcessingResult;
import com.example.demo.model.IPValidationResult;
import com.example.demo.service.DBLoggerService;
import com.example.demo.service.FileService;
import com.example.demo.service.IPValidatorService;
import com.example.demo.util.FileUploadUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class FileController {
	
	org.slf4j.Logger logger = LoggerFactory.getLogger(FileController.class);
	
	@Autowired
	private FileService fileService;
	
	@Autowired
    private HttpServletRequest request;
	
	@Autowired
	private IPValidatorService ipValidator;
	
	@Autowired
	private DBLoggerService dbLogger;
	
	@ResponseBody
	@PostMapping(path = "/v1/file")
	public ResponseEntity<InputStreamResource> upload(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        //catch input file reception time
		long start = System.currentTimeMillis();
        //process input file
        FileProcessingResult response = processFile(multipartFile);
		// catch file processing finish time
        long stop = System.currentTimeMillis();
		
		// log in db processing result and times
		dbLogger.log(start, stop, request, response);
		
		return response.responseEntity();
	}
	
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public FileProcessingResult processFile(MultipartFile multipartFile) throws IOException {
        
		String fileName = null;
		IPValidationResult validationResult = null;
		try {
			// validate if remote address is a valid one
			validationResult = ipValidator.isValid(request);
			if(!validationResult.valid()) {
				// if remote ip address is not a valid one, a corresponding status code is sent back with a message
				return new FileProcessingResult(validationResult, new ResponseEntity(validationResult.message(), HttpStatus.FORBIDDEN));
			}
			// get input file name
			fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			//save input file on disc
			Path filePath = FileUploadUtil.saveFile(fileName, multipartFile);
			//process input file
			Path result = fileService.process(filePath);
	        //delete input file from disc
			filePath.toFile().delete();
			//prepare result file for sending to user
	        InputStreamResource resource = new InputStreamResource( new CleaningFileInputStream( result.toString()));
	        // send result file to user
	        return new FileProcessingResult(validationResult, ResponseEntity.ok()
	                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + "OutcomeFile.json")
	                .contentType(MediaType.parseMediaType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
	                .body(resource));
		} catch(RuntimeException e) {
			//catch any runtime exception during processing of input file and report it back to user
			logger.error("Error processing fileName = " + fileName, e);
			return new FileProcessingResult(validationResult, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
		}
		
	}
}
