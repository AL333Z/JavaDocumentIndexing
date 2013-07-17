package it.al333z.crawler;

import it.al333z.models.Document;
import it.al333z.models.Folder;

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
    	
        List<RecursiveAction> forks = new LinkedList<RecursiveAction>();
        
        for (Folder subFolder : folder.getSubFolders()) {
            FolderSearchTask task = new FolderSearchTask(wc, subFolder);
            forks.add(task);
            task.fork();
        }
        
        for (Document document : folder.getDocuments()) {
            DocumentSearchTask task = new DocumentSearchTask(wc, document);
            forks.add(task);
            task.fork();
        }
        for (RecursiveAction task : forks) {
        	task.join();
        }
    }
}
    