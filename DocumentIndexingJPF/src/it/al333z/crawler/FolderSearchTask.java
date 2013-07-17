package it.al333z.crawler;

import it.al333z.models.Document;
import it.al333z.models.Folder;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

public class FolderSearchTask extends RecursiveAction {
    private final Folder folder;
    private final WordCrawler wc;
    
    public FolderSearchTask(WordCrawler wc, Folder folder) {
        super();
        this.wc = wc;
        this.folder = folder;
    }
    
    @Override
    protected void compute() {
    	        
        // trying with only one DocumentSearchTask, to reduce the number of scenarios
    	
		try {
			 DocumentSearchTask task = new DocumentSearchTask(wc, Document.fromFile(null));
			task.fork();
			task.join();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
    