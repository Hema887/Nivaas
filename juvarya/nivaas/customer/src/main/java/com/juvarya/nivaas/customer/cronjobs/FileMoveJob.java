package com.juvarya.nivaas.customer.cronjobs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FileMoveJob {

	Logger log = LoggerFactory.getLogger(FileMoveJob.class);

	@Scheduled(cron = "0 30 16 * * *")
	    public void moveAndDeleteFiles() {
		 log.info("Entering into class "+FileMoveJob.class);
	        Path sourceDirectory = Path.of("/home/logs/customer");
	        Path destinationDirectory = Path.of("/home/savedlogs/customer");

	        try {
	        	Files.list(sourceDirectory)
                .forEach(sourceFile -> {
                    Path destinationFile = destinationDirectory.resolve(sourceFile.getFileName());

                    try {
                        Files.copy(sourceFile, destinationFile, StandardCopyOption.REPLACE_EXISTING);
                        log.info("File copied: " + sourceFile.getFileName());
                       /* try {
                        	 Files.deleteIfExists(sourceDirectory);
                        	 log.info("file deleted :"+ sourceFile.getFileName());
						} catch (Exception e) {
							log.warn("Error deleting file :"+e.getMessage());
						} */
                       
                        
                    } catch (IOException e) {
                        log.warn("Error copying file: " + e.getMessage());
                    }
                });
	        } catch (IOException e) {
	        	log.warn("Error listing files: " + e.getMessage());
	            e.printStackTrace();
	        }
	    }
}
