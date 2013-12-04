package main;

import gui.ClientWindow;

/**
 * 
 * This is the place to launch the main client program GUI
 *
 */
public class P2PClient {
	
	public static int p2pPORT;
	
	public static void main(String[] args){
		
		new ClientWindow("Ang", "10.181.16.111");
		
	}
}
