package p2pTCP.server;

import gui.DisplayTextScrollPanel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class TCPServer implements Runnable {

    private final int PORT;
    private static final String DEFAULT_RECEIVED_FILE = System.getProperty("user.dir") +
                                                        File.separator
                                                        + "src" + File.separator + "p2pTCP" +
                                                        File.separator + "server" + File.separator +
                                                        "ReceivedFile.txt";
    private Object receivedObj = null;
    private String receivedFile = null;
    private Map<String, DisplayTextScrollPanel> nameToChatBox;
    private String peerName;
    private boolean over;

    public TCPServer(int portNum){
    	PORT = portNum;
    	over = false;
    }
    
    @SuppressWarnings("resource")
    public void runServer () {
        try {

            ServerSocket serverS = new ServerSocket(PORT, 10);

            while (!over) {

                Socket clientSocket = serverS.accept();

                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                String inType = (String) in.readObject();
                Object inObj = in.readObject();
                dealWithObjectReceived(inType, inObj);

                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                out.writeObject("Hi Client, this is server. Your information has been received");
                out.flush();
                out.close();

                clientSocket.close();
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    public synchronized void addChatWindow(String peerName, DisplayTextScrollPanel out){
    	nameToChatBox.put(peerName, out);
    }

    @SuppressWarnings("rawtypes")
    private void dealWithObjectReceived (String inType, Object inObj) {
        if (inType.equals("textfile")) {
            writeReceivedFile(inObj);
        }
        else {
            Class c = null;
            try {
                c = Class.forName(inType);
            }
            catch (ClassNotFoundException e) {
                System.out.println("Client's object type is not found...");
                return;
            }
            receivedObj = c.cast(inObj);
            System.out.println("I received object \"" + c.cast(inObj) + "\" from the client!");

            if (inType.equals("java.lang.String")){
            	String received = (String) receivedObj;
            	nameToChatBox.get(received.split(":")[0]).addText(received);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void writeReceivedFile (Object inObj) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(DEFAULT_RECEIVED_FILE));

            List<String> fileLines = (List<String>) inObj;
            for (String s : fileLines) {
                out.write(s + "\n");
            }

            out.close();
            receivedFile = DEFAULT_RECEIVED_FILE;
        }
        catch (Exception e) {
            System.out.println("Error reading client's file input or writing it to a file...");
            return;
        }

        System.out.println("I received file \"" + DEFAULT_RECEIVED_FILE + "\" from the client!");
    }

    public Object getMostRecentObject () {
        return receivedObj;
    }

    public String getMostRecentFileName () {
        return receivedFile;
    }
    
    public void turnOff(){
    	over = true;
    }

	@Override
	public void run() {
		this.runServer();
	}
}
