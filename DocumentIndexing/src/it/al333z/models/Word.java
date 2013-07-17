package it.al333z.models;

import java.io.File;

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
}
