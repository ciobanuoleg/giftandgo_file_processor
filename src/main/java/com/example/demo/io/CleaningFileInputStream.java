package com.example.demo.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.tomcat.util.http.fileupload.FileUtils;
/**
 * File InputStream class with additional responsibility to delete file on disc at close time. May be an over engineered. 
 */
public class CleaningFileInputStream extends FileInputStream {

	private String name;
	
	public CleaningFileInputStream(String name) throws FileNotFoundException {
		super(name);
		this.name = name;
	}
	
	@Override
	public void close() throws IOException {
		super.close();
		FileUtils.forceDelete(new File(name));
	}
}
