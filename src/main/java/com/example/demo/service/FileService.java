package com.example.demo.service;

import java.nio.file.Path;
/**
 * Service interface for processing of input file.
 */
public interface FileService {

	public Path process(Path filePath);
}
