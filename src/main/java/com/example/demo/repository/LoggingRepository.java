package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.repository.entity.DBLogStatement;

/**
 * DB Repository interface for logging of file processing result.
 */
public interface LoggingRepository extends JpaRepository<DBLogStatement, Long>{
	
	@Query(value = "from DBLogStatement as statement ORDER BY statement.timestamp DESC LIMIT 1", nativeQuery = false)
	DBLogStatement findLast();
	

}
