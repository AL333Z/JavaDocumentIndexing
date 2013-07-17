package it.al333z.main;

import it.al333z.indexer.SystemIndexer;
import it.al333z.ui.MainView;

public class Main {	
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		
		int numOfIndexers = Integer.parseInt(args[0]);
		int deltaTime = Integer.parseInt(args[1]);
		log("Num of indexers: "+numOfIndexers);
			
		SystemIndexer systemIndexer = new SystemIndexer(numOfIndexers, deltaTime);
		MainView mainView = new MainView();
		mainView.addListener(systemIndexer);
		systemIndexer.setView(mainView);
		
		mainView.show();
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
	
	private static void log(String msg) {
//		System.out.println("[Main] "+msg);
	}
}
