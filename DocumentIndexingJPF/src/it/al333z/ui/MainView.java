package it.al333z.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

import javax.swing.*;

public class MainView  extends JFrame implements ActionListener{
	// buttons to start/pause/stop indexing process
	private JButton startButton;
	private JButton pauseButton;
	private JButton stopButton;
	
	// file chooser to choose directory to index
	private JTextField fileChooser;
	
	// label to show index stats
	private JLabel indexLabel;
	private JLabel indexSizeLabel;
	
	// text field to input search text
	private JTextField searchTextField;
	private JButton searchButton;
	
	// list for show search results
	private JList resultList;
	private JScrollPane scrollPane;
	
	// main panel
	private JPanel controlPanel;
	
	// search panel
	private JPanel searchPanel;
	
	// search result panel
	private JPanel searchResultPanel;
	
	private ArrayList<IMainViewListener> listeners;
	
	public MainView(){
		super("Document Indexing");		
		setSize(900, 350);
	
		this.listeners = new ArrayList<IMainViewListener>();
		
		// control panel
		this.controlPanel = new JPanel();
		
		this.startButton = new JButton("Start");
		this.pauseButton = new JButton("Pause");
		this.stopButton = new JButton("Stop");
		this.indexLabel = new JLabel();
		this.indexSizeLabel = new JLabel();
		
		this.startButton.addActionListener(this);
		this.pauseButton.addActionListener(this);
		this.stopButton.addActionListener(this);
		
		this.fileChooser = new JTextField(25);
		this.fileChooser.setText("texts");
		
		this.controlPanel.add(new JLabel("Dir: "));
		this.controlPanel.add(this.fileChooser);
		this.controlPanel.add(this.startButton);
		this.controlPanel.add(this.pauseButton);
		this.controlPanel.add(this.stopButton);
		this.controlPanel.add(this.indexLabel);	
		this.controlPanel.add(this.indexSizeLabel);
		
		// search panel
		this.searchPanel = new JPanel();
		
		// search result panel
		this.resultList = new JList();
		this.searchResultPanel = new JPanel();
		
		this.searchTextField = new JTextField(15);
		this.searchButton = new JButton("Search");
		this.searchButton.addActionListener(this);
		
		this.searchPanel.add(new JLabel("Search: "));
		this.searchPanel.add(this.searchTextField);
		this.searchPanel.add(this.searchButton);
		this.searchPanel.add(this.resultList);
		
		// adding to a main panel
		JPanel cp = new JPanel();
		LayoutManager layout = new BorderLayout();
		cp.setLayout(layout);
		cp.add(BorderLayout.NORTH, this.searchPanel);
		cp.add(BorderLayout.CENTER, this.searchResultPanel);
		cp.add(BorderLayout.SOUTH, this.controlPanel);
		setContentPane(cp);		
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public void actionPerformed(ActionEvent ev){
		String cmd = ev.getActionCommand(); 
		if (cmd.equals("Start")){
			notifyStarted();
		} else if (cmd.equals("Stop")){
			notifyStopped();
		} else if (cmd.equals("Pause")){
			notifyPaused();
		} else if (cmd.equals("Search")){
			startSearch();
		}
	}

	private void notifyStarted(){
//		System.out.println("Started.");
		
		String dir = this.fileChooser.getText();
		File dirToIndex = new File(dir);
		for (IMainViewListener l: listeners){
			l.started(dirToIndex);
		}
	}
	
	private void notifyStopped(){
//		System.out.println("Stopped.");
		for (IMainViewListener l: listeners){
			l.stopped();
		}
	}	
	
	private void notifyPaused(){
//		System.out.println("Paused.");
		
		for (IMainViewListener l: listeners){
			l.paused();
		}
	}
	
	private void startSearch(){
		GuiExecutor.instance().execute(new Runnable() {
			@Override
			public void run() {
				// clean previous search
				// clean previous search
				if (scrollPane != null) {
					searchResultPanel.remove(scrollPane);
				} 
				
				resultList = new JList();
				scrollPane = new JScrollPane(resultList);
				searchResultPanel.add(scrollPane);
				
				searchResultPanel.revalidate();
				searchResultPanel.repaint();
			}
		});
		
		System.out.println("Starting search..");
		for (IMainViewListener l: listeners){
			l.startSearch(this.searchTextField.getText());
		}
	}
	
	public void addListener(IMainViewListener listener){
		this.listeners.add(listener);
	}
	
	public void removeListener(IMainViewListener listener){
		this.listeners.remove(listener);
	}
	
	public void showProgress(final int progress) {
		GuiExecutor.instance().execute(new Runnable() {
			@Override
			public void run() {
				indexSizeLabel.setText("" + progress + " indexed words.");
			}
		});
	}
	
	public void showStatus(final String status) {
		GuiExecutor.instance().execute(new Runnable() {
			@Override
			public void run() {
				indexLabel.setText(status);
			}
		});
	}
	
	public void showResults(final String[] res) {
		GuiExecutor.instance().execute(new Runnable() {
			@Override
			public void run() {			

				if (scrollPane != null) {
					searchResultPanel.remove(scrollPane);
				} 
				
				resultList = new JList(res);
				scrollPane = new JScrollPane(resultList);
				searchResultPanel.add(scrollPane);
				
				searchResultPanel.revalidate();
				searchResultPanel.repaint();
			}
		});
	}
	
}
