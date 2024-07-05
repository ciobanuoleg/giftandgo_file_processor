package com.example.demo.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.multipart.MultipartFile;
/**
 * Utility class helping to upload a file on server.
 */
public class FileUploadUtil {

	public static Path saveFile(String fileName, MultipartFile multipartFile)
            throws IOException {
        Path uploadPath = Paths.get("Files-Upload");
          
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
 
        String fileCode = RandomStringUtils.randomAlphanumeric(8);
        
        Path filePath = null;
        
        try (InputStream inputStream = multipartFile.getInputStream()) {
            filePath = uploadPath.resolve(fileCode + "-" + fileName);
            
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            
        } catch (IOException ioe) {       
            throw new IOException("Could not save file: " + fileName, ioe);
        }
         
        return filePath;
    }
}