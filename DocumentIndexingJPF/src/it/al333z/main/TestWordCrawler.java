/*
 * Fork-Join example, adapted from
 * http://www.oracle.com/technetwork/articles/java/fork-join-422606.html
 * 
 */
package it.al333z.main;

import it.al333z.crawler.WordCrawler;
import it.al333z.models.Folder;
import it.al333z.models.Word;

import java.io.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import gov.nasa.jpf.jvm.Verify;

/**
 * Verifying word crawler.
 * @author ale
 *
 */

public class TestWordCrawler {    

    public static void main(String[] args) throws IOException, InterruptedException {
    	    	
    	// starting word crawler, with dummy content
    	Verify.beginAtomic();
    	LinkedBlockingQueue<Word> retrievedWordQueue = new LinkedBlockingQueue<Word>();
    	WordCrawler wordCrawler = new WordCrawler(retrievedWordQueue);
    	// no need of indexers
        wordCrawler.setNumOfIndexers(0);
		Verify.endAtomic();
						
		wordCrawler.retrieveOccurrencesInParallel(Folder.fromDirectory(null));
		
		// checking that content is correct
		LinkedList<Word> list = new LinkedList<Word>();
		list.add(new Word("bacon", new File("/home/ale", "test.txt")));
		list.add(new Word("chocolate", new File("/home/ale", "test.txt")));
		list.add(new Word("cupcake", new File("/home/ale", "test.txt")));
		list.add(new Word("steak", new File("/home/ale", "test.txt")));
		
		while (!retrievedWordQueue.isEmpty()) {
			Word w = (Word) retrievedWordQueue.take();
			assert (list.contains(w));
		}
    }
    
    public static <T> boolean containsDuplicates(T... elements) {
        final Set<T> set = new HashSet<T>();
        Collections.addAll(set, elements);
        return set.size() < elements.length;
    }
    
}
