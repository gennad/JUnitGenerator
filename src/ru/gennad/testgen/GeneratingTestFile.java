package ru.gennad.testgen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneratingTestFile {
	
	private String sourcePath;
	private String rootDir;
	private String testPath;
	private File file;
	private FileWriter fileWriter;
	private PrintWriter printWriter;
	public String packString;
	
	public static ArrayList<String> classes = new ArrayList<String>();
	
	
	
	/**
	 * 
	 * @param sourcePath Full path to file
	 * @param rootDir
	 */
	public GeneratingTestFile(String sourcePath, String rootDir) {
		
		//get the package
		int min = rootDir.length();
		String pack = sourcePath.substring(min+1);
		pack = pack.replace('\\', '/');
		this.packString = pack;
		//cut off the file
		
		
		
		
		this.rootDir = rootDir;
		
		
		sourcePath = sourcePath.replace('\\', '/');
		rootDir = sourcePath.substring(0, sourcePath.lastIndexOf('/'));
		rootDir = rootDir.replace('\\', '/');
		if (sourcePath.endsWith("/")) {
			sourcePath = sourcePath.substring(0, sourcePath.length()-1);
		}
		
		String fileName = sourcePath.substring(sourcePath.lastIndexOf('/')+1);
		
		
		setSourcePath(sourcePath);
		//setRootDir(rootDir);
						 		
		int len = rootDir.length();
		String testPath = getRootDir()+"/tests";
		setTestPath(testPath);
		
		try {
			int dot = fileName.indexOf('.');
			fileName = fileName.substring(0, dot) + "Test" + fileName.substring(dot);
			openFileForWrite(testPath+"/"+fileName);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
		
		
		
		
		
		
		if (pack.contains(".") && pack.contains("/")) {
			pack = pack.substring(0, pack.lastIndexOf("/"));
			pack = pack.replace('/', '.');
			//write pack
			writePackage(pack);
		}
		//else don't write package
		
		//write imports
		writeImports();
		
		
//		if (!sourcePath.endsWith("/") || !sourcePath.endsWith("\\")) {
//			sourcePath += "/";
//		}
//		
//		if (!rootDir.endsWith("/") || !rootDir.endsWith("\\")) {
//			rootDir += "/";
//		}
			}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}

	public String getRootDir() {
		return rootDir;
	}
	
	public boolean createFile() {
		return true;
	}

	public void setTestPath(String testPath) {
		this.testPath = testPath;
	}

	public String getTestPath() {
		return testPath;
	}
	
	public boolean isFolderExists(String path) {
		File f = new File(path);
		return f.exists();
	}
	
	public boolean isParentFolderExists() {
		return true;
	}
	
	public boolean createFullDirectory(String path) {
		File f = new File(path);
		Stack<String> stack = new Stack<String>();
		int posSlash = 0;
		String sub;
		
		if (f.exists())
			return true;
		else {
			path = path.replace('\\', '/');
			
			while (f.exists() != true) {
				posSlash = path.lastIndexOf('/');
				String dirname = path.substring(posSlash+1);
				path = path.substring(0, posSlash);
				stack.add(dirname);
				f = new File(path);
			}
			
			File f2;
			for (int i = 0; i < stack.size(); i++) {
				path += "/"+stack.pop();
				f2 = new File(path);
				f2.mkdir();
			}
			
			
			return true;
		}
		
		
	}
	
	public boolean createFile(String fullName) throws IOException {
		fullName = fullName.replace('\\', '/');
		 
		int slash = fullName.lastIndexOf('/');
		String fileRelName = fullName.substring(slash+1);
		String fileDirName = fullName.substring(0, slash);
		
		createFullDirectory(fileDirName);
		
		int dot = fullName.indexOf('.');
		fullName = fullName.substring(0, dot) + "Test" + fullName.substring(dot);
		
		File file = new File(fullName);
		
		boolean b = file.createNewFile();
		if (b) {
			classes.add(fullName);
		}
		return b;
	}
	
	public void writeTestClassBegin(String className) {
		 		 
		 String str = 
			 "/**" + "\r\n"
			 + " *" + "\r\n"
			 + " */" + "\r\n"
			 + "public class "+className+"Test {";
		 printWriter.println(str);
		 printWriter.flush();
	}
	
	public void writeTestMethodBegin(String name, String availability, String ret/*, 
			HashMap<String, String> params, HashMap<String, String> exs*/) {
		String fLetter = name.substring(0, 1).toUpperCase();		 
		String newName = fLetter + name.substring(1);
		
		String str = 
			"\r\n" +
			"    /**" + "\r\n"
			 + "     *" + "\r\n"
			 + "     */" + "\r\n"
			+ "    "+availability + " " + ret + " test" + newName + "() {"; 
		
		printWriter.println(str);
		printWriter.println();
		printWriter.println("    }");
		printWriter.flush();
		
	}
	
	
	public void writeTypicalTestMethodContent(String name, String availability, 
			HashMap<String, String> params, HashMap<String, String> exs) {
		
	}
	
	private void openFileForWrite(String path) throws IOException {
		createFile(path);
		
		fileWriter = new FileWriter(path);
	    printWriter = new PrintWriter(fileWriter,true);	    	    
	}
	
	private void closeFile() throws IOException {
		fileWriter.close();
	}
	
	protected void finalize() {
		try {
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getMethodIndent() {
		return "    ";
	}
	
	public void writeSpaceAndBrackets(String indent) {
		printWriter.println(indent+indent);
		printWriter.println(indent+"}");
		printWriter.flush();
	}
	
	public void writeImports() {		
		printWriter.println("import junit.framework.TestCase;");
		printWriter.println();
		printWriter.println();
		printWriter.flush();
	}
	
	public void writeSuiteImports() {		
		printWriter.println("import junit.framework.Test;\r\n" +
				"import junit.framework.TestCase;\r\n" +
				"import junit.framework.TestSuite;\r\n");
	}
	
	public void writePackage(String line) {
		printWriter.println(line);		
		//write import
		String pack = line.replace("package", "import");
		printWriter.println(pack);
		printWriter.flush();
	}
	
	
	public void generateCaseFile() throws IOException {
		if (!rootDir.endsWith("/") || !rootDir.endsWith("\\")) {
			rootDir += "/";
		}
		rootDir += "tests/UnitTests.java";
		File f = new File(rootDir);
		f.createNewFile();
		
		fileWriter.close();
		
		FileWriter fileWriter1 = new FileWriter(rootDir);
	    PrintWriter printWriter1 = new PrintWriter(fileWriter1,true);
		
		String s = 
			"import "+packString+";"+"\r\n"+ 			
			"import junit.framework.Test;" + "\r\n"+
			"import junit.framework.TestCase;" + "\r\n"+
			"import junit.framework.TestSuite;"+"\r\n"

+"/**"+"\r\n"
+" * This file aggregates all of the Unit Tests for this component."+"\r\n"
+" *"+"\r\n"
+" * @author TCSDEVELOPER"+"\r\n"
+" */"+"\r\n"
+"public class UnitTests extends TestCase {" + "\r\n"
+"    /**" + "\r\n"
	+"    * Creates a test suite containing all Unit Tests" + "\r\n"
	+"    * for this component." + "\r\n"
	+"    *" + "\r\n"
	+"    * @return A test suite containing all unit tests." + "\r\n"
	+"    */" + "\r\n"
	+"    @org.junit.Test" + "\r\n"
	+"    public static Test suite() {" + "\r\n"
	+"	    TestSuite suite = new TestSuite();" + "\r\n"		
	+"	    suite.addTestSuite(";
	
	int iterator = 0;
	for (String c: this.classes) {
		String q = c.substring(c.lastIndexOf('/')+1, c.indexOf('.'));
		
		if (iterator > 0) {
			s+= ", ";
		}
		
		s += q+".class";
		iterator++;
	}
	
	s += ");" + "\r\n"
	+"	    return suite;" + "\r\n"
	+"    }" + "\r\n"
+"}" + "\r\n";
		
	
		printWriter1.println(s);
		printWriter1.flush();
		
		fileWriter1.close();
		
	}
	
	
}
