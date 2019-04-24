package mmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

import mmd.model.MMDAlgo;

public class MMD_runner {

	public static void main(String[] args) throws Exception {		
//		BasicConfigurator.configure();		
		
		// Preparing to run all tests in ./test/.
		System.out.println("RUNNING TESTS in ./test/ ");
		
		// This gets all the files in ./test/		
		File folder = new File("./test/");
		File[] listOfFiles = folder.listFiles();
		Arrays.sort(listOfFiles);
		
		// Runs through the files in ./test/ to tell you what it is going to test		
		System.out.println("Found test directories:");
		for(File f : listOfFiles) {
			System.out.println(f.toString());
			File inputFile = new File(f.toString() + "/input/");
			File outputFile = new File(f.toString() + "/output/");
			if(!inputFile.exists()) {
				throw new Exception("ERROR: There must be a directory at path " + inputFile.toString());				
			}
			for(File tf: outputFile.listFiles()) {
				if(!(tf.getName().equals("error.txt") || tf.getName().equals("pass.txt") || tf.getName().equals("skip.txt"))) {
					tf.delete();
				}
			}
		}
		
		int passedTests = 0;
		int failedTests = 0;
		int skippedTests = 0;
		
		System.out.println("");
		for(File f : listOfFiles) {
            System.out.println("####################################################################");
			System.out.println("Running test " + f.getName());
			try {
				File skip = new File(f.toString() + "/output/skip.txt");
				if(skip.exists()) {
					skippedTests++;
					continue;
				}
				
				MMDAlgo mmd = new MMDAlgo(f.toString() + "/input/", f.getName(),f.toString()+"/output/" );
		    	passedTests++;
                System.out.println("\033[32mTEST RAN (EXAMINE RESULTS)\033[0m");
			}catch (Exception e){
	            File errorFile = new File(f.toString() + "/output/error.txt");
	            File passFile = new File(f.toString() + "/output/pass.txt");
	            
	            System.out.println("MMD THREW ERROR: \"" + e.getMessage() + "\"" );
	            try {
	            	// 
		            if(errorFile.exists()) {
		            	ArrayList<String> fileLines = new ArrayList<String>();
		            	int lengthOfFile = 0;
		        		try {
		        			if(errorFile.length() == 0) throw new Exception("Please write something to " + errorFile.toString());
		        			
		        			FileReader fr = new FileReader(errorFile);
		        			BufferedReader br = new BufferedReader(fr);
		        		    String line;
		        		    while ((line = br.readLine()) != null) {
		        		    	fileLines.add(line);
		        		    	lengthOfFile++;
		        		    }
		        		    if(fileLines.get(0).equals(e.getMessage())) {
		        		    	passedTests++;
		        		    	System.out.println("Thrown error matched error in error.txt file.");
		    	                System.out.println("\033[32mPASS \033[0m");
		        		    }else {
		        		    	if(lengthOfFile == 0) {
		        		    		System.out.println("Make sure the first line of the file has the exact error message string.");
		        		    	}else {
		        		    		System.out.println("It seems we have two different errors getting trapped.");
		        		    		System.out.println("Expected: " + fileLines.get(0));
		        		    		System.out.println("GOT     : " + e.getMessage());
		        		    		e.printStackTrace(System.out);
		        		    	}
		        		    	System.out.println("\033[31mFAIL \033[0m");
		    			        failedTests++;
		        		    }
		        		    
		        		}catch(Exception e1) {
		        			e1.printStackTrace(System.out);
	        		    	System.out.println("\033[31mFAIL \033[0m");
	    			        failedTests++;
		        		}
	
		            }
		            else {
		            	System.out.println("\033[31mFAIL \033[0m");
    			        failedTests++;
		            }
		            
				}catch(Exception e2) {
					e2.printStackTrace(System.out);
			        failedTests++;
				}finally {
//    	            System.out.println("####################################################################");
				}
	            
			}
			
		}
		
		System.out.println("TOTAL PASSED: \033[32m" + passedTests +"\033[0m Total Failed: \033[31m" + failedTests + " \033[0m Total Skipped: \033[33m" + skippedTests + "\033[0m ");
	}
}