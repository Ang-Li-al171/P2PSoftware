package p2pTCP.server;

import gui.DisplayTextScrollPanel;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class TCPServer implements Runnable {

    private final int PORT;
    private Object receivedObj = null;
    private Map<String, DisplayTextScrollPanel> nameToChatBox;
    private boolean over;

    /**
     * 
     * @param portNum Port number to be used for receiving connections from other people
     */
    public TCPServer (int portNum) {
        nameToChatBox = new HashMap<String, DisplayTextScrollPanel>();
        PORT = portNum;
        over = false;
    }
    
    /**
     * sets up the server to be ready to receive TCP connections from other peers
     */
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
            
            serverS.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * used by the GUI class to associate a new Chat Window with this server
     * @param peerName name of the peer for this chat window
     * @param out the scrollable text pane in the GUI
     */
    public synchronized void addChatWindow (String peerName, DisplayTextScrollPanel out) {
        nameToChatBox.put(peerName, out);
    }

    @SuppressWarnings("rawtypes")
    private void dealWithObjectReceived (String inType, Object inObj) {

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

        if (inType.equals("java.lang.String")) {
            String received = (String) receivedObj;
            nameToChatBox.get(received.split(":")[0]).addText(received);
        }
    }

    /**
     * not used in this chatting program, but may be used in other cases
     * @return the object received most recently, already casted
     */
    public Object getMostRecentObject () {
        return receivedObj;
    }

    /**
     * shuts down this TCP server
     */
    public void turnOff () {
        over = true;
    }

    /**
     * used for running this TCP Server in a separate thread
     */
    @Override
    public void run () {
        this.runServer();
    }
}
