package it.al333z.models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Document {

	private final List<String> lines;
    private final File file;
	
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
        List<String> lines = new LinkedList<String>();
        BufferedReader reader;
        try {
        	reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
        } catch (Exception ex){
        	ex.printStackTrace();
        } 
        return new Document(lines, file);
    }
}
