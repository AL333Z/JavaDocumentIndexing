package it.al333z.ui;

import java.io.File;

public interface IMainViewListener {
	public void started(File root);
	public void paused();
	public void stopped();
	public void startSearch(String text);
}
