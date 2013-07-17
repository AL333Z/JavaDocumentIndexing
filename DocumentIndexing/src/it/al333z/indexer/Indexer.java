package it.al333z.indexer;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import it.al333z.models.Word;

/**
 * Indexer thread, that takes words from the queue passed to its constructor
 * and index-ize the words to the files in which they're contained in. 
 * @author ale
 *
 */

public class Indexer extends Thread {
	private final BlockingQueue<Word> wordQueue;
	private boolean shouldExecute;
	private ConcurrentHashMap<String, CopyOnWriteArrayList<File>> index;
	private SystemIndexer systemIndexer;
	
	public Indexer(BlockingQueue<Word> fileQueue, ConcurrentHashMap<String, CopyOnWriteArrayList<File>> index, SystemIndexer systemIndexer){
		this.wordQueue = fileQueue;
		this.index = index;
		this.systemIndexer = systemIndexer;
	}
	
	public void run(){
		shouldExecute = true;
		
		while(shouldExecute){	
			try {
				Word currentWord = wordQueue.take();
				File currentFile = currentWord.getFile();
				if (currentFile.getAbsolutePath().equals("/dev/null")) {
					shouldExecute = false;
					if (this.systemIndexer != null) this.systemIndexer.indexerCompletedIndexing();
					log("Stopped.");
				} else {
					index(currentWord.getWord(), currentFile);
					if (this.systemIndexer != null) this.systemIndexer.updateIndexSize();
				}

			} catch (InterruptedException ex){
				Thread.currentThread().interrupt();
		    }
			
			if (this.isInterrupted()) {
				this.interrupted();
				log("interrupting..");
				shouldExecute = false;
			}
		}	
		return;
	}
	
	public void index(String word, File file){
		log("Indexing word " + word +" in file: " + file.getAbsolutePath());
		if (this.index.containsKey(word)) {
			// append file in queue, if the queue doesn't already contain the file
			this.index.get(word).addIfAbsent(file);
		} else {
			// create a new list for the word
			CopyOnWriteArrayList<File> newList = new CopyOnWriteArrayList<File>();
			newList.add(file);
			
			// if no one add the word in the meantime, map the word with the list
			CopyOnWriteArrayList<File> res = this.index.putIfAbsent(word, newList);
			if (res != null && res != newList) {
				// else append the file to list
				res.addIfAbsent(file);
			}
		}
	}
	
	private void log(String msg) {
//		System.out.println("[Indexer: "+this.getName()+"] "+msg);
	}
}
