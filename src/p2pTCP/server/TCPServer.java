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

    public TCPServer (int portNum) {
        nameToChatBox = new HashMap<String, DisplayTextScrollPanel>();
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

    public Object getMostRecentObject () {
        return receivedObj;
    }

    public void turnOff () {
        over = true;
    }

    @Override
    public void run () {
        this.runServer();
    }
}
