package ru.gennad.testgen;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class TestGen {
	
	public static String fileSeparator;
	public static Logger logger = Logger.getLogger("MyLog");
	public static FileHandler fh;
	public static String stringDelimeter;
	public static GeneratingTestFile testFile;
	public static String rootDir;
	public List<String> classesList;

	
	public static void main(String[] args) {		
		System.out.println("Hello!");		
		fileSeparator = System.getProperty("file.separator");					
		try {
			fh = new FileHandler("MyLogFile.log", true);
		} catch (SecurityException e) {
			logger.log(Level.WARNING, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {			
			logger.log(Level.WARNING, e.getMessage());
			e.printStackTrace();
		}
	    logger.addHandler(fh);
	    //logger.setLevel(Level.ALL);	    
	    SimpleFormatter formatter = new SimpleFormatter();
	    fh.setFormatter(formatter);
		
		String path = null;		
		if (args.length == 1) {
			if (args[0].equals("C:\\") 
					|| args[0].equals("C:/")
					|| args[0].endsWith(".java")) {
				System.out.println("Wrong directory");
				System.exit(0);
			}
			path = args[0];
			rootDir = path;
		}
		else {
			logger.warning("Num of arguments: "+args.length);
			//logger.info(args[0]);
			System.out.println("Usage: jdcom <path to project root dir>");			
		}
		
		if (path == null) System.exit(0);
			
		listFiles(path);
		
		//String os = System.getProperty("os.name");
		//TODO stub
		stringDelimeter = "\r\n";
	
	}

	public static void listFiles(String path) {
		File f = new File(path);
		
		if (!f.exists()) 
			System.out.println("This path is invalid. Please check it and repeat.");
		
		if (f.isDirectory()) {
			String[] children = f.list();
			for (int i = 0; i < children.length; i++) {
				children[i] = path + fileSeparator + children[i];
				System.out.println(children[i]);
				listFiles(children[i]);
			}			
		}
		else {
			int unixSeparator = path.lastIndexOf("/");
			int winSeparator = path.lastIndexOf("\\");
			char separator;
			String fileName;
			
			if (unixSeparator > winSeparator) {
				
				separator = '/';
				logger.log(Level.INFO, "Separator: "+separator);
			}
			else if (winSeparator > unixSeparator) {
				separator = '\\';
				logger.log(Level.INFO, "Separator: win");
			}
			else {
				separator = '\u0000';
				logger.log(Level.INFO, "Separator: "+separator);
			}
						
			if (separator != '\u0000') {
				
				String finalSep;
				if (separator == '\\') {
					finalSep = "\\\\";
				}
				else {
					finalSep = String.valueOf(separator);
				}
				
				String[] pieces = path.split(finalSep);
				fileName = pieces[pieces.length-1];
			}
			else {
				fileName = path;
			}
			
			if (fileName.endsWith(".java") && !fileName.contains("Test")) {
				logger.log(Level.INFO, "opening: "+fileName);
				
				
				testFile = new GeneratingTestFile(path, rootDir);
				
				
				try {
					FileInputStream fstream = new FileInputStream(path);
				    // Get the object of DataInputStream
				    DataInputStream in = new DataInputStream(fstream);
				    BufferedReader br = new BufferedReader(new InputStreamReader(in));
				    String strLine;
				    String fullContent = "";
				    int lineNumber = 0;
				    boolean hasPackage = false;
				    
				    
				    String prevLine = "";

				    
				    //Read File Line By Line
				    while ((strLine = br.readLine()) != null)   {
				        // Print the content on the console
				        //System.out.println (strLine);
				    	
				    	if (isClass(strLine)) {
				    		String className = getClassName(strLine);
				    		testFile.writeTestClassBegin(className);
				    		//testFile.writeSpaceAndBrackets("");
				    	}
				    	else if (isMethod(strLine)) {
				    		String methodName = getMethodName(strLine);
				    		String returnType = getReturnType(strLine);
				    		String vis = getVisibility(strLine);
				    		testFile.writeTestMethodBegin(methodName, vis, returnType);
				    		testFile.writeSpaceAndBrackets("");
				    	}
				    	else if (isPackage(strLine)) {
				    		hasPackage = true;
				    		testFile.writePackage(strLine.trim());
				    	}
				    	else if (isImport(strLine)) {
				    		//testFile.writeImports();
//				    		if (hasPackage == false) {
//				    			testFile.wr
//				    		}
				    	}
				    }
				    //testFile.writeSpaceAndBrackets("");
				    
				    //generate case file
				    testFile.generateCaseFile();
				
				        
				}
				catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}

	public static boolean isClass(String curLine) {
		if (curLine.contains("class ")) {
        	return true;
        }
		return false;
	}
	
	public static boolean isMethod(String curLine) {
		//check if current line is public and not class and is not ending by comment				        
        if (curLine.matches(".*\\(.*\\).*") 
        			&& !curLine.contains("protected") 
        			&& !curLine.contains("private")
        			&& !curLine.contains("for ")
        			&& !curLine.contains("if ")
        			&& !curLine.contains("if(")
        			&& !curLine.contains("for(")
        			&& !curLine.contains("while(")
        			&& !curLine.contains("while (")
        			&& !curLine.contains("do {")
        			&& !curLine.contains("do{(")
        			&& !curLine.contains("*")
        			&& !curLine.trim().endsWith(";")) {
        	return true;
        }
        return false;
        
	}
	
	
	
	
	public static String getReturnType(String line) {
		int posLeftBracket = line.lastIndexOf("(");
		String subString = line.substring(0, posLeftBracket);
		
		List<String> lines = Arrays.asList(subString.split(" "));
		int len = lines.size();
		int num = len - 2;
		
		try {
			String type = lines.get(num);
			logger.info("Return type: "+type);
			return type;
		}
		catch (IndexOutOfBoundsException e) {
			return "";
		}						
	}
	
	public static String getClassName(String line) {
		String search = "class ";
		
		int posLeft = line.indexOf("class")+search.length();
		String subString = line.substring(posLeft);
		
		int posRight = subString.indexOf(" ");
		
		String ret;
		if (posRight > 0) ret = subString.substring(posLeft, posRight);
		else ret = subString;
		
		return ret;								
	}
	
	public static String getMethodName(String line) {
		int posLeftBracket = line.lastIndexOf("(");
		String subString = line.substring(0, posLeftBracket);
		subString = subString.trim();
		
		int posLeftSpace = subString.lastIndexOf(" ");
		subString = subString.substring(posLeftSpace);
		subString = subString.trim();
		return subString;
	}
	
	
	public static String getVisibility(String line) {
		
		if (line.trim().startsWith("public ")) {
			return "public";
		}
		else if (line.trim().startsWith("protected ")) {
			return "protected";
		}
		else if (line.trim().startsWith("private ")) {
			return "private";
		}
		else {
			return "";
		}
	}
	
	public static boolean isPackage(String curLine) {
		//check if current line is public and not class and is not ending by comment				        
        if (curLine.contains("package ") &&
        		!curLine.contains("* ")) {
        	return true;
        }
        return false;
        
	}
	
	public static boolean isImport(String curLine) {
		//check if current line is public and not class and is not ending by comment				        
        if (curLine.contains("import ") &&
        		!curLine.contains("* ")) {
        	return true;
        }
        return false;
        
	}
}
	
	
