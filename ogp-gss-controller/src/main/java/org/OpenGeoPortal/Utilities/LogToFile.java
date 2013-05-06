package org.OpenGeoPortal.Utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogToFile {
	
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	private String filePath;
	
	public LogToFile(String filePath){
		this.filePath = filePath;
	}
	
    public void log(String msg) {

    	PrintWriter fileLogger = null;
    	File logFile = new File(filePath);

    	try {
    		fileLogger = new PrintWriter(new FileWriter(logFile, true));
	
    		fileLogger.println(msg);
	    
    		fileLogger.flush();
	    
    		fileLogger.close();
    	} catch(IOException ioe) {
    		logger.error("IO Exception writing log file: " + ioe.getMessage());
    	}
    }
}
