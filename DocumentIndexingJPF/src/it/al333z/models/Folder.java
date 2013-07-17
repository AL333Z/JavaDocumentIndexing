package it.al333z.models;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Folder {
    private final List<Folder> subFolders;
    private final List<Document> documents;
    
    public Folder(List<Folder> subFolders, List<Document> documents) {
        this.subFolders = subFolders;
        this.documents = documents;
    }
    
    public List<Folder> getSubFolders() {
        return this.subFolders;
    }
    
    public List<Document> getDocuments() {
        return this.documents;
    }
    
    public static synchronized Folder fromDirectory(File dir) throws IOException {
        List<Document> documents = new LinkedList<Document>();
        List<Folder> subFolders = new LinkedList<Folder>();
        
        // simulating a single document..
		documents.add(Document.fromFile(null));
	
        return new Folder(subFolders, documents);
    }
}

