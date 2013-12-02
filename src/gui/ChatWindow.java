package gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import p2pTCP.client.TCPClient;
import p2pTCP.server.TCPServer;


@SuppressWarnings("serial")
public class ChatWindow extends JFrame{
	
	private JPanel mainPanel;
	private DisplayTextScrollPanel chatBox;
	private JButton sendButton;
	private JTextField input;
	
	private TCPClient myTCPClient;
	private TCPServer myTCPServer;
	
	public ChatWindow(String title, String destIP, int destPort, int serverPort){
		super();
		this.setTitle("Chatting with " + title);
        this.setLayout(new FlowLayout());
        this.mainPanel = new JPanel();
        this.mainPanel.setPreferredSize(new Dimension(400, 500));
        this.mainPanel.setLayout(new GridBagLayout());
        this.getContentPane().add(this.mainPanel);
        
		chatBox = new DisplayTextScrollPanel("chat history", "", 10, 15, false);
		
        addScrollPane();
        addTextInputField();
        addSendButton();
        
        this.validate();
        this.pack();
        this.setVisible(true);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
              myTCPServer.turnOff();
            }
          });
        
        myTCPClient = new TCPClient(destIP, destPort, 200);
    	myTCPServer = new TCPServer(serverPort, chatBox);
    	new Thread(myTCPServer).start();
	}
	
	private void addScrollPane(){
        GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.gridx = 0;
		c.gridwidth = 8;
		c.gridheight = 1;
		c.insets = new Insets(8,8,8,8);
		mainPanel.add(chatBox, c);
    }
    
    private void addTextInputField(){
    	input = new JTextField(20);
        GridBagConstraints c = new GridBagConstraints();
		c.gridy = 1;
		c.gridx = 0;
		c.gridwidth = 8;
		c.insets = new Insets(8,8,8,8);
		mainPanel.add(input, c);
    }
    
    private void addSendButton(){
    	sendButton = new JButton("Send");
    	sendButton.addActionListener(new sendListener());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.SOUTH;
		c.gridy = 2;
		c.gridx = 0;
		c.gridwidth = 8;
		c.insets = new Insets(8,8,8,8);
		mainPanel.add(sendButton, c);
    }
    
    private class sendListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			myTCPClient.sendObjectToServer("java.lang.String", input.getText() + " (this is a test message!)");
			chatBox.addText(input.getText());
		}
    }
}
