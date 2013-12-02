package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;


public class PPServer {

    public static void main (String[] args) {
        try {
            new PPServer();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public HashMap<String, String> myPPClientList = new HashMap<String, String>();

    private String convertAddressToString (byte[] ipAddress, int port) {
        String str = "";
        for (int i = 0; i<4; i++){
            if (ipAddress[i]<0){
                str += ipAddress[i]+256;
            }else{
                str += ipAddress[i];
            }
            if (i!=3){
                str +=".";
            }
        }
        return str +  "-" + port;
    }

    private String packListIntoString () {
        String list = "";
        int index = 0;
        for (String addr : myPPClientList.keySet()) {
            list += addr + ":" + myPPClientList.get(addr);
            if (index != myPPClientList.size()-1){
                list += ';';
            }
            index ++;
        }
        System.out.println(myPPClientList.size());
        System.out.println(myPPClientList);
        
        return list;
    }

    public PPServer () throws IOException {

        DatagramSocket serverSocket = new DatagramSocket(3333);
        byte[] receiveData = new byte[4096];
        byte[] sendData = new byte[32768];
        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData,
                    receiveData.length);
            serverSocket.receive(receivePacket);
            String sentence = new String(receivePacket.getData());
            String[] strs = sentence.split(":");
            if (strs.length == 2) {
                if (strs[0].equals("Connection Request")) {
                    System.out.println("RECEIVED: " + sentence);

                    InetAddress IPAddress = receivePacket.getAddress();
                    int port = receivePacket.getPort();
                    
                    String key = convertAddressToString(IPAddress.getAddress(),
                            port);
                    String name = strs[1];
                    System.out.println(myPPClientList.containsKey(key));
                    if (myPPClientList.containsKey(key)){
                        myPPClientList.remove(key);
                    }
                    if (myPPClientList.containsValue(name)){
                        for(String k: myPPClientList.keySet()){
                            if (myPPClientList.get(k).equals(name)){
                                myPPClientList.remove(k);
                            }
                        }
                    }
                    myPPClientList.put(key,name);
                    String list = packListIntoString();
                    sendData = list.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData,
                            sendData.length, IPAddress, port);
                    serverSocket.send(sendPacket);
                }
            }
        }
    }
}