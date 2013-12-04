package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;


public class P2PUDPClient {
    
    private final int CONNECT = 1;
    private final int DISCONNECT = 2;
    private Map<String, String> myPeerList = new HashMap<String, String>();
    private String myName;
    private String hostIP;

    public P2PUDPClient (String ip, String name) {
        hostIP = ip;
        myName = name;
    }

    private void unpackList (String list) {
        list = list.substring(0, list.length() - 1);
        String[] strs = list.split(";");
        for (String s : strs) {
            myPeerList.put(s.split(":")[1].trim(), s.split(":")[0].trim());
        }
    }

    public Map<String, String> connectAndGetPeerList () {
        try {
            createSocketAndSend("Connection Request From : " + myName, CONNECT);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return myPeerList;
    }
    
    public void disconnect(){
        try {
            createSocketAndSend("Disconnection Request From : " + myName, DISCONNECT);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void createSocketAndSend(String msg, int flag) throws IOException{
        DatagramSocket clientSocket = new DatagramSocket();
        String[] ipNumbers = hostIP.split("\\.");
        byte[] ip = {(byte) Integer.parseInt(ipNumbers[0]),
                     (byte) Integer.parseInt(ipNumbers[1]),
                     (byte) Integer.parseInt(ipNumbers[2]),
                     (byte) Integer.parseInt(ipNumbers[3]) };
        InetAddress IPAddress = InetAddress.getByAddress(ip);
        byte[] sendData = new byte[2048*8];
        byte[] receiveData = new byte[2048*8];
        String requestMessage = msg;
        sendData = requestMessage.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData,
                                                       sendData.length, IPAddress,
                                                       P2PUDPServer.UDPSERVER_PORT);
        clientSocket.send(sendPacket);
        DatagramPacket receivePacket = new DatagramPacket(receiveData,
                                                          receiveData.length);
        clientSocket.receive(receivePacket);
        String peerList = new String(receivePacket.getData());
        System.out.println("Response Message From Server : " + peerList);
        if (flag == CONNECT){
            unpackList(peerList);
        }
        clientSocket.close();
    }
}
