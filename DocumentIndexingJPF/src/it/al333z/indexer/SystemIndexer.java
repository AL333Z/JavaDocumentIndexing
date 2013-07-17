package it.al333z.indexer;

import it.al333z.crawler.WordCrawler;
import it.al333z.models.Folder;
import it.al333z.models.Word;
import it.al333z.ui.IMainViewListener;
import it.al333z.ui.MainView;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SystemIndexer implements IMainViewListener {
	
	private int numberOfIndexers;
	private int deltaTime;
	
	private Indexer[] indexers;
	private BlockingQueue<Word> retrievedWordQueue;
	private ConcurrentHashMap<String, CopyOnWriteArrayList<File>> index;
	private File root;
	private WordCrawler wordCrawler;
	
	private boolean isStarted;
	private boolean isPaused;
	private boolean isStopped;
	
	private int numCompletedIndexer;
	
	private MainView view;

	private ExecutorService backgroundExec;
	private ScheduledExecutorService scheduledExec;
	
	public SystemIndexer(int numberOfIndexers, int deltaTime){
		this.numberOfIndexers = numberOfIndexers;
		this.deltaTime = deltaTime;
		
		this.indexers = new Indexer[this.numberOfIndexers];
		this.numCompletedIndexer = 0;
		
		this.isStarted = false;
		this.isPaused = false;
		this.isStopped = false;
		
		this.backgroundExec = Executors.newCachedThreadPool();
			
		this.scheduledExec = null;
	}
	
	public synchronized void indexerCompletedIndexing(){
		this.numCompletedIndexer++;
				
		if (this.numberOfIndexers == this.numCompletedIndexer) {
			this.numCompletedIndexer = 0;
			this.isStarted = false;
			
			this.view.showStatus("Completed.");
			
			log("Indexing complete. Will be rescheduled in "+ this.deltaTime + "seconds.");
		}
	}
	
	public synchronized void updateIndexSize() {
		this.view.showProgress(this.index.size());
	}
	
	public void started(File rootDir) {
		this.root = rootDir;
		
		/* 
		 * the user previously stopped the computation and want to re-start, 
		 * set isStopped flag to false and clean old executor
		 */
		if (this.isStopped) {
			this.scheduledExec = null;
			this.isStopped = false;
		}
		
		/*
		 * initialize the scheduled executor and start the computation,
		 * starting indexers threads and wordCrawler (with its tasks)
		 */
		if (this.scheduledExec == null) {
			this.scheduledExec = Executors.newSingleThreadScheduledExecutor();
			this.retrievedWordQueue = new LinkedBlockingQueue<Word>(10);
			
			this.wordCrawler = new WordCrawler(retrievedWordQueue);
			this.wordCrawler.setNumOfIndexers(numberOfIndexers);
			
			this.scheduledExec.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {
					if (isStopped) {
						return;
					}
					
					log("starting..");
					if (!isStarted) {
						view.showStatus("Started..");
							
						isStarted = true;
						isPaused = false;
						
						numCompletedIndexer = 0;
							
						// just create a new index, for now
						index = new ConcurrentHashMap<String, CopyOnWriteArrayList<File>>();
						
						// starting indexers
						for (int i = 0; i < numberOfIndexers ; i++) {
							indexers[i] = new Indexer(retrievedWordQueue, index, SystemIndexer.this);
							indexers[i].start();
						}
						
						// starting word crawler (and tasks)
						try {
							wordCrawler.retrieveOccurrencesInParallel(Folder.fromDirectory(root));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}, 0, this.deltaTime, TimeUnit.SECONDS);
		}
	}
	
	public void paused(){
		backgroundExec.execute(new Runnable() {
			@Override
			public void run() {
				
				if (isStarted) {
					for (int i = 0; i < numberOfIndexers ; i++) {
						if (isPaused) {
							// resume indexers
							indexers[i] = new Indexer(retrievedWordQueue, index, SystemIndexer.this);
							indexers[i].start();
						} else {
							// interrupt indexers
							indexers[i].interrupt();
						}	
					}
					
					// update ui
					if (isPaused) {
						view.showStatus("Resumed..");
					} else {
						view.showStatus("Paused.");
					}
					
					isPaused = !isPaused;
				}
			}
		});
	}
	
	public void stopped(){
		backgroundExec.execute(new Runnable() {
			@Override
			public void run() {
				
				// shutdown scheduled task
				scheduledExec.shutdownNow();
				
				// interrupt indexers
				if (isStarted) {
					for (int i = 0; i < numberOfIndexers ; i++) {
						indexers[i].interrupt();
					}
					isStarted = false;	
				}
				
				isPaused = false;
				isStopped = true;
				view.showStatus("Stopped.");
			}
		});
	}
	
	public void startSearch(final String text) {
		backgroundExec.execute(new Runnable() {
			@Override
			public void run() {
				CopyOnWriteArrayList<File> res = index.get(text);
				String[] fileNames = new String[0];
				
				if (res != null) {
					File[] files = (File[]) res.toArray(new File[0]);
					
					fileNames = new String[files.length];
					for(int i = 0 ; i < files.length; i++) {
						File file = files[i];
						fileNames[i] = file.getAbsolutePath();
					}
				}
				
				view.showResults(fileNames);
			}
		});
	}
	
	public void setView(MainView v){
		this.view = v;
	}
	
	public static Character[] toCharacterArray(String s) {
		if (s == null) {
			return null;
		}
	   Character[] array = new Character[s.length()];
	   for (int i = 0; i < s.length(); i++) {
		      array[i] = new Character(s.charAt(i));
	   }

	   return array;
	}
	
	private void log(String msg) {
		System.out.println("[SystemIndexer: "+this+"] "+msg);
	}
}
