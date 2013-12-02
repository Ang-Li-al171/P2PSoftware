package udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

public class PPClient {

	private HashMap<String, String> myPPClientList = new HashMap<String, String>();
	private String myName;
	private String hostIP;

	public PPClient(String ip, String name) {
		myName = name;
		hostIP = ip;
	}

	private void unpackList(String list) {
		list = list.substring(0, list.length()-1);
		String[] strs = list.split(";");
		for (String s : strs){
			myPPClientList.put(s.split(":")[1].trim(), s.split(":")[0].trim());
		}
	}

	public HashMap<String, String> getMap() {
		try {
			DatagramSocket clientSocket = new DatagramSocket();
			String[] ipNumbers = hostIP.split("\\.");
			byte[] ip = { (byte) Integer.parseInt(ipNumbers[0]),
						  (byte) Integer.parseInt(ipNumbers[1]),
						  (byte) Integer.parseInt(ipNumbers[2]),
						  (byte) Integer.parseInt(ipNumbers[3]) };
			InetAddress IPAddress = InetAddress.getByAddress(ip);
			byte[] sendData = new byte[4096];
			byte[] receiveData = new byte[32768];
			String sentence = "Connection Request:" + myName;
			sendData = sentence.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData,
					sendData.length, IPAddress, 3333);
			clientSocket.send(sendPacket);
			DatagramPacket receivePacket = new DatagramPacket(receiveData,
					receiveData.length);
			clientSocket.receive(receivePacket);
			String list = new String(receivePacket.getData());
			System.out.println("FROM SERVER:" + list);
			unpackList(list);
			clientSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return myPPClientList;
	}
}