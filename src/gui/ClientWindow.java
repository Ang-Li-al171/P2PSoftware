package gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import p2pTCP.server.TCPServer;
import udp.P2PUDPClient;


@SuppressWarnings("serial")
public class ClientWindow extends JFrame {

    private JPanel mainPanel;
    private JButton connectButton;
    private JButton disconnectBututon;
    private JButton peerConnectButton;
    private JComboBox<Object> peerChoice;
    private Map<String, String> listOfPeers;
    private String myName;

    private TCPServer myTCPServer;
    private P2PUDPClient myUDPClient;

    /**
     * 
     * @param name name to appear in the p2p system
     * @param udpServer IP address of the p2p server
     */
    public ClientWindow (String name, String udpServer) {
        super();
        this.setTitle("My Fancy-looking P2P Client Program");
        this.myName = name;
        this.setLayout(new FlowLayout());
        this.mainPanel = new JPanel();
        this.mainPanel.setPreferredSize(new Dimension(600, 300));
        this.mainPanel.setLayout(new GridBagLayout());
        this.getContentPane().add(this.mainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addServerButtons();
        addAvailablePeers();
        addConnectToPeer();
        
        this.validate();
        this.pack();
        this.setVisible(true);

        myUDPClient = new P2PUDPClient(udpServer, myName);
    }

    private void addServerButtons () {
        connectButton = new JButton("Connect to P2P Server");
        connectButton.addActionListener(new GetListOfPeers());
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.gridx = 0;
        c.insets = new Insets(8, 8, 8, 8);
        mainPanel.add(connectButton, c);
        
        disconnectBututon = new JButton("Disconnect from P2P Server");
        disconnectBututon.addActionListener(new Disconnect());
        GridBagConstraints c2 = new GridBagConstraints();
        c2.gridy = 0;
        c2.gridx = 1;
        c2.insets = new Insets(8, 8, 8, 8);
        mainPanel.add(disconnectBututon, c2);
    }

    private void addAvailablePeers () {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 1;
        c.gridx = 0;
        c.insets = new Insets(8, 8, 8, 8);
        mainPanel.add(new JLabel("Available Peers:"), c);

        peerChoice = new JComboBox<Object>(new String[] { "List of Peers Unavailable" });
        GridBagConstraints c1 = new GridBagConstraints();
        c1.gridy = 1;
        c1.gridx = 1;
        c1.insets = new Insets(8, 8, 8, 8);
        mainPanel.add(peerChoice, c1);
    }

    private void addConnectToPeer () {
        peerConnectButton = new JButton("Connect to this Peer");
        peerConnectButton.addActionListener(new newChat());
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 1;
        c.gridx = 2;
        c.insets = new Insets(8, 8, 8, 8);
        mainPanel.add(peerConnectButton, c);
    }

    private void setListOfPeers (Map<String, String> m) {
        mainPanel.remove(peerChoice);
        peerChoice = new JComboBox<Object>(m.keySet().toArray());
        GridBagConstraints c1 = new GridBagConstraints();
        c1.gridy = 1;
        c1.gridx = 1;
        c1.insets = new Insets(8, 8, 8, 8);
        mainPanel.add(peerChoice, c1);
        mainPanel.validate();
        mainPanel.repaint();
    }

    private class newChat implements ActionListener {
        @Override
        public void actionPerformed (ActionEvent e) {
            String peerName = peerChoice.getSelectedItem().toString();
            String[] ipAndPort = listOfPeers.get(peerName).split("-");

            // init TCP server if necessary
            if (myTCPServer == null) {
                myTCPServer =
                        new TCPServer(Integer.parseInt(listOfPeers.get(myName).split("-")[1]));
                new Thread(myTCPServer).start();
            }

            new ChatWindow(myName, peerName, ipAndPort[0], Integer.parseInt(ipAndPort[1]),
                           myTCPServer);
        }
    }

    private class GetListOfPeers implements ActionListener {
        @Override
        public void actionPerformed (ActionEvent e) {
            listOfPeers = myUDPClient.connectAndGetPeerList();
            setListOfPeers(listOfPeers);
        }
    }
    
    private class Disconnect implements ActionListener {
        @Override
        public void actionPerformed (ActionEvent e) {
            myUDPClient.disconnect();
            peerChoice.removeAllItems();
            peerChoice.addItem("List of Peers Unavailable");
            peerChoice.setSelectedIndex(0);
        }
    }

}
