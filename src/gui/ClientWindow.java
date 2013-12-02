package gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import udp.PPClient;

@SuppressWarnings("serial")
public class ClientWindow extends JFrame{
	
	private JPanel mainPanel;
	private JButton connectButton;
	private JButton peerConnectButton;
	private JComboBox<Object> peerChoice;
	private PPClient myUDPClient;
	private HashMap<String, String> listOfPeers;
	private String myName;
	
	/**
	 * 
	 * @param name name to appear in the p2p system
	 * @param udpServer IP address of the p2p server
	 */
	public ClientWindow(String name, String udpServer){
		super();
		this.setTitle("My Fancy-looking P2P Client Program");
		this.myName = name;
        this.setLayout(new FlowLayout());
        this.mainPanel = new JPanel();
        this.mainPanel.setPreferredSize(new Dimension(600, 300));
        this.mainPanel.setLayout(new GridBagLayout());
        this.getContentPane().add(this.mainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        addServerButton();
        addAvailablePeers();
        addConnectToPeer();
        
        this.validate();
        this.pack();
        this.setVisible(true);

		myUDPClient = new PPClient(udpServer, myName);
	}
	
	private void addServerButton(){
		connectButton = new JButton("Connect to P2P Server");
		connectButton.addActionListener(new getListOfPeers());
        GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.gridx = 0;
		c.gridwidth = 3;
		c.insets = new Insets(8,8,8,8);
		c.anchor = GridBagConstraints.NORTH;
		mainPanel.add(connectButton, c);
	}
	
	private void addAvailablePeers(){
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 1;
		c.gridx = 0;
		c.insets = new Insets(8,8,8,8);
		mainPanel.add(new JLabel("Available Peers:"), c);
		
		peerChoice = new JComboBox<Object>(new String[]{"List of Peers Unavailable"});
        GridBagConstraints c1 = new GridBagConstraints();
		c1.gridy = 1;
		c1.gridx = 1;
		c1.insets = new Insets(8,8,8,8);
		mainPanel.add(peerChoice, c1);
	}
	
	private void addConnectToPeer(){
		peerConnectButton = new JButton("Connect to this Peer");
		peerConnectButton.addActionListener(new newChat());
        GridBagConstraints c = new GridBagConstraints();
		c.gridy = 1;
		c.gridx = 2;
		c.insets = new Insets(8,8,8,8);
		mainPanel.add(peerConnectButton, c);
	}
	
	private void setListOfPeers(HashMap<String, String> m){
		mainPanel.remove(peerChoice);
		peerChoice = new JComboBox<Object>(m.keySet().toArray());
        GridBagConstraints c1 = new GridBagConstraints();
		c1.gridy = 1;
		c1.gridx = 1;
		c1.insets = new Insets(8,8,8,8);
		mainPanel.add(peerChoice, c1);
		mainPanel.validate();
		mainPanel.repaint();
	}
	
	private class newChat implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			String peerName = peerChoice.getSelectedItem().toString();
			String[] ipAndPort = listOfPeers.get(peerName).split("-");
			new ChatWindow(myName, peerName, ipAndPort[0], Integer.parseInt(ipAndPort[1]),
					Integer.parseInt(listOfPeers.get(myName).split("-")[1]));
		}
	}
	
	private class getListOfPeers implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			listOfPeers = myUDPClient.getMap();
			setListOfPeers(listOfPeers);
		}
	}
	
}