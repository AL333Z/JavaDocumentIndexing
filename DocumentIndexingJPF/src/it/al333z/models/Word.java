package it.al333z.models;

import java.io.File;

import com.sun.org.apache.regexp.internal.recompile;

public class Word {

	private String word;
	private File file;
	
	public Word(String word, File file){
		this.word = word;
		this.file = file;
	}
	
	public String getWord(){
		return this.word;
	}
	
	public File getFile(){
		return this.file;
	}
	
	@Override
	public boolean equals(Object obj) {
		Word w = (Word)obj;
		return this.word.equals(w.getWord()) && this.file.getAbsolutePath().equals(w.getFile().getAbsolutePath());		
	}
	
	
	
}
