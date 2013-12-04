package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class P2PUDPServer {

    public static final int UDPSERVER_PORT = 4869;
    
    private Map<String, String> myPPClientList;
    private boolean over;

    /**
     * public constructor, initialize instance variables
     */
    public P2PUDPServer () {
        over = false;
        myPPClientList = new HashMap<String, String>();
    }
    
    /**
     * entry point for the java application
     * @param args
     */
    public static void main (String[] args) {
        try {
            new P2PUDPServer().launchServer();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String convertAddressToString (byte[] ipAddress, int port) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            if (ipAddress[i] < 0) {
                str.append(ipAddress[i] + 256);
            }
            else {
                str.append(ipAddress[i]);
            }
            if (i < 3) {
                str.append(".");
            }
        }
        return str.append("-" + port).toString();
    }

    private String packListIntoString () {
        StringBuilder list = new StringBuilder();
        int index = 0;
        for (String name : myPPClientList.keySet()) {
            list.append(myPPClientList.get(name) + ":" + name);
            if (index != myPPClientList.size() - 1) {
                list.append(';');
            }
            index++;
        }
        return list.toString();
    }
    
    /**
     * this call sets up the server to be ready to receive messages
     * @throws IOException
     */
    public void launchServer () throws IOException {
        
        DatagramSocket serverSocket = new DatagramSocket(UDPSERVER_PORT);

        while (!over) {
            byte[] receiveData = new byte[2048*8];
            byte[] sendData = new byte[2048*8];

            DatagramPacket receivePacket = new DatagramPacket(receiveData,
                                                              receiveData.length);
            serverSocket.receive(receivePacket);
            String receivedMessage = new String(receivePacket.getData());
            String[] strs = receivedMessage.split(":");
            
            if (strs.length == 2) {
                if (strs[0].trim().equals("Connection Request From")) {
                    InetAddress IPAddress = receivePacket.getAddress();
                    int port = receivePacket.getPort();

                    String key = convertAddressToString(IPAddress.getAddress(),
                                                        port).trim();
                    String name = strs[1].trim();

                    if (!myPPClientList.containsKey(name)) {
                        myPPClientList.put(name, key);
                    }
                    
                    String list = packListIntoString();
                    sendData = list.getBytes();
                    DatagramPacket sendPacket =
                            new DatagramPacket(sendData,
                                               sendData.length, IPAddress, port);
                    serverSocket.send(sendPacket);
                }
                
                else if (strs[0].trim().equals("Disconnection Request From")){
                    InetAddress IPAddress = receivePacket.getAddress();
                    int port = receivePacket.getPort();

                    String name = strs[1].trim();

                    if (myPPClientList.containsKey(name)) {
                        myPPClientList.remove(name);
                    }
                    
                    String reply = "You're now disconnected!";
                    sendData = reply.getBytes();
                    DatagramPacket sendPacket =
                            new DatagramPacket(sendData,
                                               sendData.length, IPAddress, port);
                    serverSocket.send(sendPacket);
                }
            }
            
            else{
                System.out.println("Something went wrong trying to process a received message...");
            }
            
        }
        serverSocket.close();
    }

    /**
     * shuts the server down
     */
    public void turnOff () {
        over = true;
    }
}
