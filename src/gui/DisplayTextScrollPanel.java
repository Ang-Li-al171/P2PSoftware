package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;


/**
 * default class used for displaying text in a scroll panel
 *
 */
@SuppressWarnings("serial")
public class DisplayTextScrollPanel extends JPanel{

	protected JLabel myLabel;
	protected String myLabelString;
	protected JScrollPane myScrollPane;
	protected JTextArea myTextArea;
	protected String myTextAreaString;

	private int myRowSize;
	private int myColSize;
	private boolean myEditable;

	Highlighter hilit;
	Highlighter.HighlightPainter painter;

	/**
	 * 
	 * @param labelText string of the text to be put on the label
	 * @param textAreaText starting text to be put in the text area
	 * @param row num of rows for the text area
	 * @param col num of cols for the text area
	 * @param edit set the text area editable or not
	 */
	public DisplayTextScrollPanel(String labelText, String textAreaText, int row, int col, boolean edit){
		myLabelString = labelText;
		myTextAreaString = textAreaText;
		myRowSize = row;
		myColSize = col;
		myEditable = edit;
		
		initComponents();

		setPreferredSize(new Dimension(300, 300));
		
		myTextArea.setFont(new Font("arial", Font.ITALIC, 20));
	}
	
	
	/**
	 * set specific text for the entire text area
	 * @param s
	 */
	public void setText(String s){
		myTextArea.setText(s);
	}
	
	/**
	 * add a line of string to the textbox
	 * @param s
	 */
	public void addText(String s){
		myTextArea.setText(myTextArea.getText() + "\n" + s);
	}
	
	/**
	 * 
	 * @return the current text in the text area
	 */
	public String getText(){
		return myTextArea.getText();
	}

	private void initComponents(){

		hilit = new DefaultHighlighter();
		painter = new DefaultHighlighter.DefaultHighlightPainter(Color.LIGHT_GRAY);

		myTextArea = new JTextArea();
		myTextArea.setColumns(myColSize);
		myTextArea.setLineWrap(true);
		myTextArea.setRows(myRowSize);
		myTextArea.setWrapStyleWord(true);
		myTextArea.setEditable(myEditable);
		myTextArea.setHighlighter(hilit);
		myTextArea.setText(myTextAreaString);
		myScrollPane = new JScrollPane(myTextArea);

		myLabel = new JLabel(myLabelString);
		myLabel.setLabelFor(myScrollPane);
		
		add(myLabel);
		add(myScrollPane);
	}
}
