package it.al333z.crawler;

import it.al333z.models.Document;

import java.util.concurrent.RecursiveAction;

public class DocumentSearchTask extends RecursiveAction{
    
	private final Document document;
    private final WordCrawler wc;
    
    public DocumentSearchTask(WordCrawler wc, Document document) {
        super();
        this.document = document;
        this.wc = wc;
    }
    
    @Override
    protected void compute() {
        wc.retrieveOccurrences(document);
    }
}

