package com.highplume.service;

/**
 * Created by Hugh on 6/17/2017.
 * Intended for utilities.
 */
 
import java.io.*;
import java.net.URI;
import java.net.URLDecoder; 
import java.util.*;

 public class Util {
 
    public static String logFile = "highplumeService.log";
    public static String logTrigger = "highplumelog.trigger";
 
    public static void log (String prefix, String output){
		log(prefix, output, 0);
	}
		

    public static void log (String prefix, String output, int logLevel){
//        String path = ROService.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		File f1 = new File("."); 				// f is the current directory; where the JVM was launched  => C:\Users\Latitude Owner\Documents\payara41\glassfish\domains\domain1\config\.
		String path = f1.getAbsolutePath();
        String decodedPath = "";
        try{
            decodedPath = URLDecoder.decode(path, "UTF-8");
        }catch (UnsupportedEncodingException e){
             e.printStackTrace();
        } 

		decodedPath = decodedPath.substring(0, decodedPath.indexOf("config")) + "logs\\";
		
        File f = new File(decodedPath+logTrigger);				//if logTrigger file exists, log everything.  Otherwise only log level 0 stuff.
        if(f.exists() || logLevel == 0) {
            Calendar calendar = Calendar.getInstance();
            java.util.Date now = calendar.getTime();

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(decodedPath+prefix+logFile, true))) {
                bw.write(now.toString() + ":" + output);
                bw.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
 }