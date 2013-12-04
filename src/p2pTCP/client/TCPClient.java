package p2pTCP.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TCPClient {

    private final String HOSTIP;
    private final int PORT;
    private final int TIMEOUT;
    private Socket s;

    /**
     * 
     * @param hostIp IP address of the host one wants to connect to
     * @param portNum Port number of the host one wants to connect to
     * @param timeOut Connection timeout value
     */
    public TCPClient (String hostIp, int portNum, int timeOut) {
        HOSTIP = hostIp;
        PORT = portNum;
        TIMEOUT = timeOut;
    }

    /**
     * sends a object of specified type to the host specified in the class constructor
     * @param outType type of object to be cast in at the receiving side, expressed in String
     * @param outObj the actual object to be sent
     */
    public void sendObjectToServer (String outType, Object outObj) {
        
        try {
            createSocketAndSend(outType, outObj);
        }
        catch (Exception e) {
            System.out.println("Something went wrong trying to send the object...");
        }
        
    }

    private void createSocketAndSend (String outType, Object outObj) throws IOException,
                                                                    ClassNotFoundException {

        s = new Socket();
        s.connect(new InetSocketAddress(HOSTIP, PORT), TIMEOUT);

        ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
        out.writeObject(outType);
        out.writeObject(outObj);
        out.flush();

        ObjectInputStream in = new ObjectInputStream(s.getInputStream());
        String message = (String) in.readObject();
        System.out.println(message);

        s.close();
    }
}
