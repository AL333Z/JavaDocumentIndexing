/*
 * Fork-Join strategy to retrieve word occurences in a document, adapted from
 * http://www.oracle.com/technetwork/articles/java/fork-join-422606.html
 * 
 */
package it.al333z.crawler;

import it.al333z.models.Document;
import it.al333z.models.Folder;
import it.al333z.models.Word;

import java.io.File;
import java.util.concurrent.*;

/**
 * WordCrawler starts the computation, starting from the root directory.
 * @author ale
 *
 */
public class WordCrawler {    

    private final ForkJoinPool forkJoinPool = new ForkJoinPool();
    private BlockingQueue<Word> retrievedWordQueue;
    private int numOfIndexers;
    
    public WordCrawler(BlockingQueue<Word> retrievedWordQueue) {
		this.retrievedWordQueue = retrievedWordQueue;
		this.numOfIndexers = 0;
	}
    
	public void setNumOfIndexers(int num){
		this.numOfIndexers = num;
	}
    
    public String[] wordsIn(String line) {
        return line.trim().split("(\\s|\\p{Punct})+");
    }
    
    public void retrieveOccurrences(Document document) {
        for (String line : document.getLines()) {
            for (String word : wordsIn(line)) {
            	Word retrievedWord = new Word(word, document.getFile());
            	try {
					retrievedWordQueue.put(retrievedWord);
					log("Retrieved word " + word + "in file " + document.getFile().getName());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
        return;
    }
        
    public void retrieveOccurrencesInParallel(Folder folder) {
        long startTime = System.currentTimeMillis();
    	
        forkJoinPool.invoke(new FolderSearchTask(this, folder));

        if (this.retrievedWordQueue != null) {	
            // when completed, put poison pills in queue to stop indexers
    		for (int i = 0; i < this.numOfIndexers ; i++) {
    			try {
       				this.retrievedWordQueue.put(new Word("", new File("/dev/null")));
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		}
		}
        
        long stopTime = System.currentTimeMillis();
        long elapsedTime = (stopTime - startTime);
        log("elapsed time: " + elapsedTime);
    }
    
	private void log(String msg) {
//		System.out.println("[WordCrawler: "+this+"] "+msg);
	}

}
