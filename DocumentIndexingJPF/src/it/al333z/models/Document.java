package it.al333z.models;

import gov.nasa.jpf.jvm.Verify;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Document {

	private final List<String> lines;
    private final File file;
	
    private static final String[] foo1 = {
		  "bacon chocolate bacon cupcake steak.", 
		};
        
    public Document(List<String> lines, File file) {
        this.lines = lines;
        this.file = file;
    }
    
    public List<String> getLines() {
        return this.lines;
    }
    
    public File getFile() {
    	return this.file;
    }
    
    // synchronized class method, to reduce I/O disk time
    public static synchronized Document fromFile(File file) throws IOException {
        
        // returning dummy content, to simulate..
        return new Document(getRandomLines(), getRandomFile());
    }
    
    /**
     * Utility methods
     */
    private static File getRandomFile(){
    	return new File("/home/ale", "test.txt");
    }
    
    private static List<String> getRandomLines(){    	
    	return Arrays.asList(foo1);
    }
}
